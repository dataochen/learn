package util;

import java.io.Serializable;

/**
 * @author dataochen
 * @Description
 * @date: 2020/11/5 16:38
 */
public class FastErrorResponse implements Serializable {
    private static final long serialVersionUID = 7509631377826589526L;

    public FastErrorResponse(boolean status, String errorMsg) {
        this.status = status;
        this.errorMsg = errorMsg;
    }

    /**
     * 调用状态 是否成功
     */
    private boolean status;
    /**
     * 失败的原因错误描述
     */
    private String errorMsg;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
