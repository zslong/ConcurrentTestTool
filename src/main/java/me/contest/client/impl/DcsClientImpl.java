package me.contest.client.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.contest.client.DcsClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static me.contest.utils.HttpUtils.*;

/**
 * Created by shilong.zhang on 2017/12/18.
 */
@Component
@Slf4j
public class DcsClientImpl implements DcsClient {
    @Value("${dcs.url.getContractListPagination}")
    private String getContractListPaginationUrl;

    @Value("${dcs.url.getDcsDealLog}")
    private String getDcsDealLogUrl;

    @Value("${dcs.url.addContractList}")
    private String addContractListUrl;

    @Value("${dcs.url.updateContractList}")
    private String updateContractListUrl;

    @Value("${dcs.url.updateContractListSilent}")
    private String updateContractListSilentUrl;

    @Value("${dcs.url.softDeleteContractList}")
    private String softDeleteContractListUrl;

    @Value("${dcs.url.updateContractConfirmStatus}")
    private String updateContractConfirmStatusUrl;

    @Value("${dcs.url.submitContractList}")
    private String submitContractListUrl;

    @Value("${dcs.url.relateSubmitContractList}")
    private String relateSubmitContractListUrl;

    @Value("${dcs.url.setContractListUrgentStatus}")
    private String setContractListUrgentStatusUrl;

    @Value("${dcs.url.destroyContract}")
    private String destroyContractUrl;

    @Value("${dcs.url.getOnOffStatus}")
    private String getDcsSwitchUrl;

    @Override
    public Map<String, Object> getContractListPagination(String deskName, Map<String, String[]> queryParams,
                                                         String pageNum, String pageSize, String sortedField,
                                                         String startegy) {
        Map<String, Object> result = Maps.newHashMap();
        try(CloseableHttpClient client = HttpClients.custom().build()) {
            HttpPost post = new HttpPost(getContractListPaginationUrl);
            List<NameValuePair> entity = Lists.newArrayList();
            entity.add(new BasicNameValuePair("productName", deskName));
            entity.add(new BasicNameValuePair("params", JSON.toJSONString(queryParams)));
            entity.add(new BasicNameValuePair("pageNum", pageNum));
            entity.add(new BasicNameValuePair("pageSize", pageSize));
            entity.add(new BasicNameValuePair("sortedField", sortedField));
            entity.add(new BasicNameValuePair("strategy", startegy));
            post.setEntity(new UrlEncodedFormEntity(entity, Consts.UTF_8));

            try (CloseableHttpResponse response = client.execute(post)) {
                JSONObject respJson = processDcsResponse("getContractListPagination", response);
                result.put("result", respJson.getOrDefault("result", Lists.newArrayList())); // contract list
                result.put("length", respJson.getOrDefault("length", 0));
            }
        } catch (IOException ex) {
            handleHttpRequestException("getContractListPagination", ex);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getDcsDealLog(String deskName, String contractId) {
        List<Map<String, Object>> result = Lists.newArrayList();
        try(CloseableHttpClient client = HttpClients.custom().build()) {
            HttpPost post = new HttpPost(getDcsDealLogUrl);
            List<NameValuePair> entity = Lists.newArrayList();
            entity.add(new BasicNameValuePair("product", deskName));
            entity.add(new BasicNameValuePair("contractId", contractId));
            post.setEntity(new UrlEncodedFormEntity(entity, Consts.UTF_8));

            try (CloseableHttpResponse response = client.execute(post)) {
                JSONObject respJson = processDcsResponse("getDcsDealLog", response);
                String dealLogStr = respJson.getString("result");
                if ("false".equals(dealLogStr)){
                    log.error("GetDcsDealLog error -- deskName: {}, contractId: {}", deskName, contractId);
                    return result;
                }
                result = jsonArrayToList(dealLogStr);
            }
        } catch (IOException ex) {
            handleHttpRequestException("getDcsDealLog", ex);
        }
        return result;
    }

    @Override
    public boolean addContractList(String deskName, String contractList, String operator) {
        try(CloseableHttpClient client = HttpClients.custom().build()) {
            HttpPost post = new HttpPost(addContractListUrl);
            //post.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            List<NameValuePair> entity = Lists.newArrayList();
            entity.add(new BasicNameValuePair("productName", deskName));
            entity.add(new BasicNameValuePair("contractList", contractList));
            entity.add(new BasicNameValuePair("operator", operator));
            post.setEntity(new UrlEncodedFormEntity(entity, Consts.UTF_8));

            try (CloseableHttpResponse response = client.execute(post)) {
                JSONObject respJson = processDcsResponse("addContractList", response);
                return respJson.getBoolean("result");
            }
        } catch (IOException ex) {
            handleHttpRequestException("addContractList", ex);
        }
        return false;
    }

    @Override
    public boolean updateContractList(String deskName, String contractList, String operator, boolean isSilent) {
        try(CloseableHttpClient client = HttpClients.custom().build()) {
            HttpPost post = new HttpPost(isSilent ? updateContractListSilentUrl : updateContractListUrl);
            List<NameValuePair> entity = Lists.newArrayList();
            entity.add(new BasicNameValuePair("productName", deskName));
            entity.add(new BasicNameValuePair("contractList", contractList));
            entity.add(new BasicNameValuePair("operator", operator));
            post.setEntity(new UrlEncodedFormEntity(entity, Consts.UTF_8));

            try (CloseableHttpResponse response = client.execute(post)) {
                JSONObject respJson = processDcsResponse("updateContractList", response);
                return respJson.getBoolean("result");
            }
        } catch (IOException ex) {
            handleHttpRequestException("updateContractList", ex);
        }
        return false;
    }

    @Override
    public boolean softDeleteContractList(String deskName, String contractIdList, String operator) {
        try(CloseableHttpClient client = HttpClients.custom().build()) {
            HttpPost post = new HttpPost(softDeleteContractListUrl);
            List<NameValuePair> entity = Lists.newArrayList();
            entity.add(new BasicNameValuePair("productName", deskName));
            entity.add(new BasicNameValuePair("contractIdList", contractIdList));
            entity.add(new BasicNameValuePair("operator", operator));
            post.setEntity(new UrlEncodedFormEntity(entity, Consts.UTF_8));

            try (CloseableHttpResponse response = client.execute(post)) {
                JSONObject respJson = processDcsResponse("softDeleteContractList", response);
                return respJson.getBoolean("result");
            }
        } catch (IOException ex) {
            handleHttpRequestException("softDeleteContractList", ex);
        }
        return false;
    }

    @Override
    public boolean updateContractConfirmStatus(String deskName, String contractId, String confirmIndex, String confirmStatus, String operator) {
        try(CloseableHttpClient client = HttpClients.custom().build()) {
            HttpPost post = new HttpPost(updateContractConfirmStatusUrl);
            List<NameValuePair> entity = Lists.newArrayList();
            entity.add(new BasicNameValuePair("productName", deskName));
            entity.add(new BasicNameValuePair("contractId", contractId));
            entity.add(new BasicNameValuePair("confirmIndex", confirmIndex));
            entity.add(new BasicNameValuePair("confirmStatus", confirmStatus));
            entity.add(new BasicNameValuePair("operator", operator));
            post.setEntity(new UrlEncodedFormEntity(entity, Consts.UTF_8));

            try (CloseableHttpResponse response = client.execute(post)) {
                JSONObject respJson = processDcsResponse("updateContractConfirmStatus", response);
                return respJson.getBoolean("result");
            }
        } catch (IOException ex) {
            handleHttpRequestException("updateContractConfirmStatus", ex);
        }
        return false;
    }

    @Override
    public boolean submitContractList(String deskName, String contractIdList, String operator) {
        try(CloseableHttpClient client = HttpClients.custom().build()) {
            HttpPost post = new HttpPost(submitContractListUrl);
            List<NameValuePair> entity = Lists.newArrayList();
            entity.add(new BasicNameValuePair("productName", deskName));
            entity.add(new BasicNameValuePair("contractIdList", contractIdList));
            entity.add(new BasicNameValuePair("operator", operator));
            post.setEntity(new UrlEncodedFormEntity(entity, Consts.UTF_8));

            try (CloseableHttpResponse response = client.execute(post)) {
                JSONObject respJson = processDcsResponse("submitContractList", response);
                return respJson.getBoolean("result");
            }
        } catch (IOException ex) {
            handleHttpRequestException("submitContractList", ex);
        }
        return false;
    }

    @Override
    public boolean relateSubmitContractList(String deskName, String contractIdList, String operator, String comments) {
        try(CloseableHttpClient client = HttpClients.custom().build()) {
            HttpPost post = new HttpPost(relateSubmitContractListUrl);
            List<NameValuePair> entity = Lists.newArrayList();
            entity.add(new BasicNameValuePair("productName", deskName));
            entity.add(new BasicNameValuePair("contractIdList", contractIdList));
            entity.add(new BasicNameValuePair("operator", operator));
            entity.add(new BasicNameValuePair("comments", comments));
            post.setEntity(new UrlEncodedFormEntity(entity, Consts.UTF_8));

            try (CloseableHttpResponse response = client.execute(post)) {
                JSONObject respJson = processDcsResponse("relateSubmitContractList", response);
                return respJson.getBoolean("result");
            }
        } catch (IOException ex) {
            handleHttpRequestException("relateSubmitContractList", ex);
        }
        return false;
    }

    @Override
    public boolean setContractListUrgentStatus(String deskName, String contractIdList, String urgentStatus, String operator) {
        try(CloseableHttpClient client = HttpClients.custom().build()) {
            HttpPost post = new HttpPost(setContractListUrgentStatusUrl);
            List<NameValuePair> entity = Lists.newArrayList();
            entity.add(new BasicNameValuePair("productName", deskName));
            entity.add(new BasicNameValuePair("contractIdList", contractIdList));
            entity.add(new BasicNameValuePair("urgentStatus", urgentStatus));
            entity.add(new BasicNameValuePair("operator", operator));
            post.setEntity(new UrlEncodedFormEntity(entity, Consts.UTF_8));

            try (CloseableHttpResponse response = client.execute(post)) {
                JSONObject respJson = processDcsResponse("setContractListUrgentStatus", response);
                return respJson.getBoolean("result");
            }
        } catch (IOException ex) {
            handleHttpRequestException("setContractListUrgentStatus", ex);
        }
        return false;
    }

    @Override
    public boolean destroyContract(String deskName, String contractIdList, String operator, String destroyReason) {
        try(CloseableHttpClient client = HttpClients.custom().build()) {
            HttpPost post = new HttpPost(destroyContractUrl);
            List<NameValuePair> entity = Lists.newArrayList();
            entity.add(new BasicNameValuePair("productName", deskName));
            entity.add(new BasicNameValuePair("contractIdList", contractIdList));
            entity.add(new BasicNameValuePair("operator", operator));
            entity.add(new BasicNameValuePair("destroyReason", destroyReason));
            post.setEntity(new UrlEncodedFormEntity(entity, Consts.UTF_8));

            try (CloseableHttpResponse response = client.execute(post)) {
                JSONObject respJson = processDcsResponse("destroyContract", response);
                return respJson.getBoolean("result");
            }
        } catch (IOException ex) {
            handleHttpRequestException("destroyContract", ex);
        }
        return false;
    }

    public boolean getDcsSwitch(){
        boolean result = false;
        try (CloseableHttpClient client = HttpClients.custom().build()){
            HttpGet get = new HttpGet(getDcsSwitchUrl);
            get.setHeader("Accept", "application/json");
            try (CloseableHttpResponse response =client.execute(get)){
                JSONObject respJson = processDcsResponse(getDcsSwitchUrl, response);
                result = respJson.getBoolean("onOff");
                log.info("getDcsSwitch");
            }
        } catch (Exception ex) {
            handleHttpRequestException(getDcsSwitchUrl, ex);
        }

        return result;
    }
}
