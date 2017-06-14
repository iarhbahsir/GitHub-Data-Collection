package todoReader;

public class Todo {
	
	private String content;
	private String fileName;
	private String creationCommitHash;
	private String deletionCommitHash;
	private String timeOfCreation;
	private String timeOfDeletion;
	
	public Todo(String content, String fileName)
	{
		this.content = content;
		this.fileName = fileName;
		this.deletionCommitHash = "Todo is incomplete";
	}
	
	public Todo(String content, String fileName, String creationCommitHash, String timeOfCreation)
	{
		this.content = content;
		this.fileName = fileName;
		this.creationCommitHash = creationCommitHash;
		this.timeOfCreation = timeOfCreation;
		this.deletionCommitHash = "Todo is incomplete";
	}
	
	@Override
	public String toString() {
		return "Content: " + content + "  File Name: " + fileName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((deletionCommitHash == null) ? 0 : deletionCommitHash.hashCode());
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Todo other = (Todo) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (deletionCommitHash == null) {
			if (other.deletionCommitHash != null)
				return false;
		} else if (!deletionCommitHash.equals(other.deletionCommitHash))
			return false;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		return true;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getCreationCommitHash() {
		return creationCommitHash;
	}

	public void setCreationCommitHash(String creationCommitHash) {
		this.creationCommitHash = creationCommitHash;
	}

	public String getDeletionCommitHash() {
		return deletionCommitHash;
	}

	public void setDeletionCommitHash(String deletionCommitHash) {
		this.deletionCommitHash = deletionCommitHash;
	}

	public String getTimeOfCreation() {
		return timeOfCreation;
	}

	public void setTimeOfCreation(String timeOfCreation) {
		this.timeOfCreation = timeOfCreation;
	}

	public String getTimeOfDeletion() {
		return timeOfDeletion;
	}

	public void setTimeOfDeletion(String timeOfDeletion) {
		this.timeOfDeletion = timeOfDeletion;
	}
	
	
}
