package me.contest.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by shilong.zhang on 2017/12/18.
 */
public interface DcsClient {
    Map<String, Object> getContractListPagination(String deskName, Map<String, String[]> queryParams,
                                                  String pageNum, String pageSize, String sortedField, String strategy);

    List<Map<String, Object>> getDcsDealLog(String product, String contractId);

    boolean addContractList(String deskName, String contractList, String operator);

    boolean updateContractList(String deskName, String contractList, String operator, boolean isSilent);

    boolean softDeleteContractList(String deskName, String contractIdList, String operator);

    boolean updateContractConfirmStatus(String deskName, String contractId, String confirmIndex, String confirmStatus, String operator);

    boolean submitContractList(String deskName, String contractIdList, String operator);

    boolean relateSubmitContractList(String deskName, String contractIdList, String operator, String comments);

    boolean setContractListUrgentStatus(String deskName, String contractIdList, String urgentStatus, String operator);

    boolean destroyContract(String deskName, String contractIdList, String operator, String destroyReason);

    boolean getDcsSwitch();
}
