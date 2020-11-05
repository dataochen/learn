package util;

import java.io.Serializable;

/**
 * @author dataochen
 * @Description
 * @date: 2020/11/5 16:38
 */
public class FastErrorResponse implements Serializable {
    public FastErrorResponse(boolean status, String errorMsg) {
        this.status = status;
        this.errorMsg = errorMsg;
    }

    private boolean status;
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
