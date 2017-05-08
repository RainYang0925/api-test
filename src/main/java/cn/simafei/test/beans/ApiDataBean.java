package cn.simafei.test.beans;

import cn.simafei.test.handler.ResponseHandler;
import cn.simafei.test.utils.StringUtil;

public class ApiDataBean extends BaseBean {
	private boolean run;
	private String caseScene; // 用例场景
	private String interfaceDesc; // 接口描述
	private String url;
	private String method;
	private String param;
    private String body;
	private boolean contains;
	private String status;
	private String verify;
	private String save;
	private String preParam;
	private String handler;

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getCaseScene() {
		return caseScene;
	}

	public void setCaseScene(String caseScene) {
		this.caseScene = caseScene;
	}

	public String getInterfaceDesc() {
		return interfaceDesc;
	}

	public void setInterfaceDesc(String interfaceDesc) {
		this.interfaceDesc = interfaceDesc;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public boolean isContains() {
		return contains;
	}

	public void setContains(boolean contains) {
		this.contains = contains;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getVerify() {
		return verify;
	}

	public void setVerify(String verify) {
		this.verify = verify;
	}

	public boolean isRun() {
		return run;
	}

	public void setRun(boolean run) {
		this.run = run;
	}

	public String getSave() {
		return save;
	}

	public void setSave(String save) {
		this.save = save;
	}

	public String getPreParam() {
		return preParam;
	}

	public void setPreParam(String preParam) {
		this.preParam = preParam;
	}

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isPostJson() {
        return "post".equalsIgnoreCase(method) && StringUtil.isNotEmpty(body);
    }

	@Override
	public String toString() {
		if (StringUtil.isEmpty(this.caseScene)) {
			return String.format("desc:%s,method:%s,url:%s,param:%s,body:%s",
					this.interfaceDesc, this.method, this.url, this.param,this.body);
		} else {
			return String.format("sence:%s,desc:%s,method:%s,url:%s,param:%s,body:%s",this.caseScene,
					this.interfaceDesc, this.method, this.url, this.param,this.body);
		}
	}

}
