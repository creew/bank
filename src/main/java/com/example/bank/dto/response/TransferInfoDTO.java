package com.example.bank.dto.response;

import com.example.bank.entity.Transfer;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

public class TransferInfoDTO implements Serializable {

    private static final long serialVersionUID = -3587601335790657712L;

    @JsonProperty("executed_date")
    private Date executedDate;

    @JsonProperty("amount")
    private Long amount;

    @JsonProperty("credentials_to")
    private String credentialsTo;

    public Date getExecutedDate() {
        return executedDate;
    }

    public void setExecutedDate(Date executedDate) {
        this.executedDate = executedDate;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getCredentialsTo() {
        return credentialsTo;
    }

    public void setCredentialsTo(String credentialsTo) {
        this.credentialsTo = credentialsTo;
    }

    public static TransferInfoDTO fromTransfer(Transfer transfer) {
        TransferInfoDTO transferInfoDTO = new TransferInfoDTO();
        transferInfoDTO.executedDate = transfer.getTimeExecuted();
        transferInfoDTO.amount = transfer.getAmount();
        transferInfoDTO.credentialsTo = transfer.getCardTo().getUser().getPrincipal();
        return transferInfoDTO;
    }

    @Override
    public String toString() {
        return "TransferInfoDTO{" +
                "executedDate=" + executedDate +
                ", amount=" + amount +
                ", credentialsTo='" + credentialsTo + '\'' +
                '}';
    }
}
