package examples.domain.dhome;

import java.util.Date;

import com.easysql.core.Entity;
import com.easysql.handlers.EntityFilter;


@SuppressWarnings("serial")
public class RequestCommand extends Entity {

	private Long id;
	private Integer type;// 1平台，2引擎
	private String url;// 请求地址;

	// 1:初次返回结果，表示已收到命今
	// 2:已发送命令
	// 3:最终处理结果已返回
	// 4：异常
	private Integer state;// 请求状态()

	private String message;
	private Date createTime;
	
	@Override
	public EntityFilter notTake() {
		return null;
	}
	

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public RequestCommand() {
		super();
	}

	public RequestCommand(Long id, Integer type, String url, Integer state,
			String message) {
		super();
		this.id = id;
		this.type = type;
		this.url = url;
		this.state = state;
		this.message = message;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
