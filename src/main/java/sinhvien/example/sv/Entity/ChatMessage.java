package sinhvien.example.sv.Entity;

public class ChatMessage {
	private String content;
	private String sender;

	public String getProblem() {
		return problem;
	}

	public void setProblem(String problem) {
		this.problem = problem;
	}

	private String problem;
	private MessageType type;

	public enum MessageType {
		CHAT, LEAVE, JOIN
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

}
