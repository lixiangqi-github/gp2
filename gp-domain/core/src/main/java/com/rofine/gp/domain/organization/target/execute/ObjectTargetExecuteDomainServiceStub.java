package com.rofine.gp.domain.organization.target.execute;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.client.RestTemplate;

import com.rofine.gp.domain.organization.target.TargetException;
import com.rofine.gp.domain.organization.target.domain.EvaluateVO;
import com.rofine.gp.domain.organization.target.domain.FillVO;
import com.rofine.gp.domain.organization.target.domain.ObjectTargetExecuteVO;
import com.rofine.gp.domain.organization.target.domain.ObjectTargetVO;
import com.rofine.gp.domain.organization.target.domain.TargetStatVO;
import com.rofine.gp.domain.organization.target.service.ObjectTargetExecuteDomainService;
import com.rofine.gp.platform.bean.ApplicationContextUtil;
import com.rofine.gp.platform.user.User;
import com.rofine.gp.platform.util.DateUtil;
import com.rofine.gp.platform.util.JsonUtil;

public class ObjectTargetExecuteDomainServiceStub implements ObjectTargetExecuteDomainService {

	@Autowired
	@LoadBalanced
	private RestTemplate restTemplate;

	private String serviceUrl;
	
	private final static String urlPrefix = "/rest";

	protected Logger logger = Logger.getLogger(ObjectTargetExecuteDomainServiceStub.class.getName());

	public static ObjectTargetExecuteDomainService getBean() {
		return (ObjectTargetExecuteDomainService) ApplicationContextUtil.getApplicationContext().getBean(
				"objectTargetExecuteDomainService");
	}

	public ObjectTargetExecuteDomainServiceStub(String serviceUrl) {
		this.serviceUrl = serviceUrl.startsWith("http") ? serviceUrl : "http://" + serviceUrl;
		this.serviceUrl += urlPrefix;
	}

	public void createExecutes(String frequencyType, ObjectTargetVO objectTarget) throws TargetException {
		restTemplate.postForObject(this.serviceUrl + "/create/executes/{frequencyType}", objectTarget,
				ObjectTargetVO.class, frequencyType);
	}

	public void startExecutes(String schemeId) {
		restTemplate.postForObject(this.serviceUrl + "/start/executes/scheme/{schemeId}", null, String.class, schemeId);

	}

	public List<ObjectTargetExecuteVO> getFillingExecutes(String schemeId, User user) throws TargetException {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("schemeId", schemeId);
			params.put("user", JsonUtil.toJson(user));

			return Arrays.asList(restTemplate.getForObject(this.serviceUrl + "/scheme/{schemeId}/fill?user={user}",
					ObjectTargetExecuteVO[].class, params));
		} catch (Exception e) {
			throw new TargetException(e);
		}

	}

	public List<ObjectTargetExecuteVO> getEvaluatingExecutes(String schemeId, User user) throws TargetException {

		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("schemeId", schemeId);
			params.put("user", JsonUtil.toJson(user));

			return Arrays.asList(restTemplate.getForObject(this.serviceUrl + "/scheme/{schemeId}/evaluate?user={user}",
					ObjectTargetExecuteVO[].class, params));
		} catch (Exception e) {
			throw new TargetException(e);
		}
	}

	public List<ObjectTargetExecuteVO> getOperatedExecutes(String schemeId, User user) {
		return null;
	}

	@Override
	public List<ObjectTargetExecuteVO> getExecutesByObjectTarget(String objectTargetId) {
		return Arrays.asList(restTemplate.getForObject(this.serviceUrl + "/object/target/{objectTargetId}/executes",
				ObjectTargetExecuteVO[].class, objectTargetId));
	}

	@Override
	public List<ObjectTargetExecuteVO> getExecutesByIds(List<String> ids) throws TargetException {
		try {
			return Arrays.asList(restTemplate.getForObject(this.serviceUrl + "/executes/{ids}/ids",
					ObjectTargetExecuteVO[].class, JsonUtil.toJsonArray(ids)));
		} catch (Exception e) {
			throw new TargetException(e);
		}
	}

	public List<ObjectTargetExecuteVO> getRemindExecutes() {
		Date sysDate = DateUtil.getSysDate();
		return null;
	}

	public void fill(String schemeId, List<FillVO> fills, User user) throws TargetException {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("schemeId", schemeId);
			params.put("user", JsonUtil.toJson(user));

			restTemplate.postForObject(this.serviceUrl + "/scheme/{schemeId}/fill?user={user}", fills, List.class,
					params);
		} catch (Exception e) {
			throw new TargetException(e);
		}

	}

	/**
	 * @param evaluates
	 * @param userId
	 * @throws TargetException
	 * @roseuid 573A8DF9021C
	 */
	public void evaluate(String schemeId, List<EvaluateVO> evaluates, User user) throws TargetException {

		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("schemeId", schemeId);
			params.put("user", JsonUtil.toJson(user));

			restTemplate.postForObject(this.serviceUrl + "/scheme/{schemeId}/evaluate?user={user}", evaluates,
					List.class, params);
		} catch (Exception e) {
			throw new TargetException(e);
		}

	}

	public void deleteExecutes(List<String> executeIds) {

	}

	/**
	 * @param schemeId
	 * @roseuid 573BC90901EE
	 */
	public List<TargetStatVO> getTargetStats(String schemeId) {
		return Arrays.asList(restTemplate.getForObject(this.serviceUrl + "/scheme/{schemeId}/monitor",
				TargetStatVO[].class, schemeId));
	}

	@Override
	public void closeExecutes(String schemeId) {
		restTemplate.postForObject(this.serviceUrl + "/close/executes/scheme/{schemeId}", null, String.class, schemeId);
	}
}
