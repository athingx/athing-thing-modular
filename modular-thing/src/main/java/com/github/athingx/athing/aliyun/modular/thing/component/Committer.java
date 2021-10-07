package com.github.athingx.athing.aliyun.modular.thing.component;

/**
 * 提交器
 */
public interface Committer {

    /**
     * 提交本次变更
     * <pre>
     *     1. 只能提交一次，第二次提交返回false
     *     2. 提交和回滚只能有一个成功
     * </pre>
     *
     * @return TRUE | FALSE
     */
    boolean commit();

    /**
     * 回滚本次变更
     * <pre>
     *     1. 只能回滚一次，第二次回滚返回false
     *     2. 提交和回滚只能有一个成功
     * </pre>
     *
     * @param step 步骤
     * @param desc 描述
     * @return TRUE | FALSE
     */
    boolean rollback(int step, String desc);

    /**
     * 回滚本次变更
     *
     * @param step  步骤
     * @param cause 异常
     * @return TRUE | FALSE
     */
    boolean rollback(int step, Throwable cause);

}
