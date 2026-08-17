package com.lcf.cloud.service;

public interface AccountService {

    /**
     * 扣减账户余额
     * @param userId
     * @param money
     */
    void decrease(Long userId, Long money);
}
