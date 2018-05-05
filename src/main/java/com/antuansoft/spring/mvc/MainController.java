/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.antuansoft.spring.mvc;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.antuansoft.mongodb.domain.Campaign;
import com.antuansoft.mongodb.domain.Country;
import com.antuansoft.mongodb.domain.User;
import com.antuansoft.mongodb.repositories.CampaignRepositoryDao;
import com.antuansoft.mongodb.repositories.CountryRepositoryDao;
import com.antuansoft.mongodb.repositories.UserRepositoryDao;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;


/**
 *
 * @author kushal
 */
@Controller
public class MainController{
	
	private static String API_KEY = "65e8f1d6bf2646419f8f856367fd392a";
	private static String OPEN_CURR_URL = "https://openexchangerates.org/";
	
/*	@Autowired
	private HttpServletRequest context;
 */
	@Autowired
	private  UserRepositoryDao userRepositoryDao;
	@Autowired
	private  CampaignRepositoryDao campaignRepositoryDao;
	@Autowired
	private  CountryRepositoryDao countryRepositoryDao;
	@Autowired
	private  EhCacheManagerFactoryBean ehcache;
	
	
/*	static{
		ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		userRepositoryDao = (UserRepositoryDao) context.getBean("UserRepositoryDao");
		campaignRepositoryDao = (CampaignRepositoryDao) context.getBean("CampaignRepositoryDao");
		countryRepositoryDao = (CountryRepositoryDao) context.getBean("CountryRepositoryDao");
		 ((ConfigurableApplicationContext)context).close();
	}
*/
	Iterable<Campaign> campaings;
	
/*	@Autowired
    private UserRegistrationDaoImpl dao;*/
	
	private static final Logger logger = Logger.getLogger(MainController.class);

	

	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String defaultPage(ModelMap map) {
		return "redirect:/menu";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(ModelMap model) {
		return "login";
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(ModelMap model) {
		return "logout";
	}
	
	@RequestMapping(value = "/accessdenied")
	public String loginerror(ModelMap model) {
		model.addAttribute("error", "true");
		return "denied";
	}
	
	@RequestMapping(value = "/menu", method = RequestMethod.GET)
	public String menu(ModelMap map) {
		if(campaings!= null) {
			map.addAttribute("camp",campaings);
		} else {
		Iterable<Campaign> campaings= campaignRepositoryDao.findAll();
		map.addAttribute("camp",campaings);
		}
		return "menu";
	}

	@RequestMapping(value = "/listUsers", method = RequestMethod.GET)
	public String listUsers(ModelMap map) {
		
		Iterable<User> users= userRepositoryDao.findAll();
		map.addAttribute("users", users);
		
		return "listUsers";
	}
	
	
	@RequestMapping(value = "/currRates", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String listCurrRates(ModelMap map) {
		RestTemplate restTemplate = new RestTemplate();
		 BasicDBObject result = (BasicDBObject) JSON.parse(restTemplate.getForObject("https://openexchangerates.org/api/latest.json?app_id=65e8f1d6bf2646419f8f856367fd392a", String.class));
		 map.addAttribute("rates", filterdata((BasicDBObject) result.get("rates")));
		return "currRates";
	}
	
	
	@RequestMapping(value = "/hisRates", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String listHisRates(ModelMap map, @RequestParam("date") String date) {
		 Object result = getHistoricalRates(date);
		 
		 map.addAttribute("rates", result);
		 
		return "hisRates";
	}
	
	@Cacheable(value="historyFindCache", key="#date")
	public Object getHistoricalRates(String date){
		RestTemplate restTemplate = new RestTemplate();
		Element e2 = null;
		BasicDBObject temp = null;
		Cache cache = ehcache.getObject().getCache("historyFindCache");
		if((e2 = cache.get(date)) == null) {
		temp =  (BasicDBObject) JSON.parse(restTemplate.getForObject("https://openexchangerates.org/api/historical/"+date+".json?app_id=65e8f1d6bf2646419f8f856367fd392a", String.class));
		Object o = filterdata((BasicDBObject) temp.get("rates"));
		Element e = new Element(date, o);
		cache.putIfAbsent(e);
		saveHistoryDate(date, o);
		return o;
		} 
		return e2.getObjectValue();
	}
	


	private Object filterdata(BasicDBObject basicDBObject) {
		//return JSON.parse("{\"EUR\":"+basicDBObject.get("EUR")+",\"GBP\":"+basicDBObject.get("GBP")+",\"INR\":"+basicDBObject.get("INR")+",\"NZD\":"+basicDBObject.get("NZD")+",\"AUD\":"+basicDBObject.get("AUD")+",\"JPY\":"+basicDBObject.get("JPY")+",\"HUF\":"+basicDBObject.get("HUF")+"}");

		return JSON.parse("{EUR:"+basicDBObject.get("EUR")+",GBP:"+basicDBObject.get("GBP")+",INR:"+basicDBObject.get("INR")+",NZD:"+basicDBObject.get("NZD")+",AUD:"+basicDBObject.get("AUD")+",JPY:"+basicDBObject.get("JPY")+",HUF:"+basicDBObject.get("HUF")+"}");
	}

	private void slowQuery(long seconds){
		try {
	        Thread.sleep(seconds);
	    } catch (InterruptedException e) {
	        throw new IllegalStateException(e);
	    }
	}
	
	private void saveHistoryDate(String date, Object data) {
		Iterable<Campaign> camp = campaignRepositoryDao.findAll();
		Iterator<Campaign> itr = camp.iterator();
		Campaign c;
		boolean noSave = false;
		while(itr.hasNext()){
			c= itr.next();
			if(c.getDate().equals(date)){
				noSave = true;
				break;
			}
		}
		if(!noSave) {
		Campaign cam = new Campaign(UUID.randomUUID().toString(),"History data :", date, data);
		campaignRepositoryDao.save(cam);
		Cache cache = ehcache.getObject().getCache("history");
		cache.removeAll();
		}
	}

	
	@RequestMapping(value = "/listCampaigns", method = RequestMethod.GET)
	public String listCampaigns(ModelMap map) {
		
		//map.addAttribute("new_campaign", new Campaign());
		campaings = campaignRepositoryDao.findAll();
		//createCache(campaings);
		map.addAttribute("campaigns",campaings);
		
		return "listCampaigns";
	}
	
	private void createCache(Iterable<Campaign> campaings) {
		Cache cache = ehcache.getObject().getCache("historyFindCache");
		if(null == cache){
			ehcache.getObject().addCache("historyFindCache");
		}
		Iterator<Campaign> itr = campaings.iterator();
		Campaign cam;
		while((cam = itr.next())!= null){
			 Element e = new Element(cam.getDate(), cam.getData()) ;
			 cache.putIfAbsent(e);
		}
		
	}

	@PreAuthorize("hasRole('ROLE_CAMPAIGN')")
	@RequestMapping(value = "/addCampaing", method = RequestMethod.POST)
	public String addCampaign(@ModelAttribute(value = "new_campaign") Campaign new_campaign,
			BindingResult result){
		
		new_campaign.setId(UUID.randomUUID().toString());
		campaignRepositoryDao.save(new_campaign);
		
		return "redirect:/listCampaigns";
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String registerUser(ModelMap map) {
		Iterable<Country> country= countryRepositoryDao.findAll();
		map.addAttribute("countries",((ArrayList<Country>) country).get(0).getCountry());
		return "register";
	}
	
	@RequestMapping(value = "/saveUser", method = RequestMethod.GET)
	public void saveUser(@ModelAttribute("user") User user, BindingResult result) {
		user.setId(UUID.randomUUID().toString());
		user.setRole(1);
		userRepositoryDao.save(user);  
	}
}

