package todoReader;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class Todo implements Comparable<Todo>{
	
	private String content;
	private String fileName;
	private String creationCommitHash;
	private String deletionCommitHash;
	private OffsetDateTime timeOfCreation;
	private OffsetDateTime timeOfDeletion;
	private String fullContent;
	
	private static final DateTimeFormatter timeFormat = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
	
	public Todo(String content, String fileName)
	{
		setContent(content);
		setFileName(fileName);
	}
	
	public Todo(String content, String fileName, String creationCommitHash, String timeOfCreation)
	{
		setContent(content);
		setFileName(fileName);
		setTimeOfCreation(timeOfCreation);
		setCreationCommitHash(creationCommitHash);
	}

	@Override
	public int compareTo(Todo toCompare) {
		
		if(!(getContent().compareTo((toCompare).getContent()) == 0)) {
			return getContent().compareTo((toCompare).getContent());
		}
		if(!(getFileName().compareTo((toCompare).getFileName()) == 0)) {
			return getFileName().compareTo((toCompare).getFileName());
		}
		
		if(getTimeOfDeletion() == null && toCompare.getTimeOfDeletion() == null) {
			return 0;
		}
		
		if(getTimeOfDeletion() == null) {
			return -1;
		}
		
		if(toCompare.getTimeOfDeletion() == null) {
			return 1;
		}
		
		return getTimeOfDeletion().compareTo((toCompare).getTimeOfDeletion());
		
	}
	
	private String elapsedTime(OffsetDateTime from, OffsetDateTime to) {
		
		Duration timeDur = Duration.between(from.toOffsetTime(), to.toOffsetTime());
		Period timePer = Period.between(from.toLocalDate(), to.toLocalDate());
		return timePer.toString() + "; " + timeDur.toString();
	}
	
	public String toString() {
		return "Content: " + getFullContent() + "\nFile Name: " + getFileName()
			+ "\nTime Of Creation: " + getTimeOfCreation() + "\nTime Of Deletion: " + getTimeOfDeletion()
			+ "\nCreation Commit Hash: " + getCreationCommitHash() + "\nDeletion Commit Hash: " + getDeletionCommitHash()
			+ "\nTime To Complete: " + ((timeOfCreation == null || timeOfDeletion == null) ? "Incomplete":(elapsedTime(timeOfCreation, timeOfDeletion)))
			+ "\n\n";
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
		this.fullContent = content;
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

	public OffsetDateTime getTimeOfCreation() {
		return timeOfCreation;
	}

	public void setTimeOfCreation(String timeOfCreation) {
		this.timeOfCreation = OffsetDateTime.parse(timeOfCreation, timeFormat);
	}

	public OffsetDateTime getTimeOfDeletion() {
		return timeOfDeletion;
	}

	public void setTimeOfDeletion(String timeOfDeletion) {
		this.timeOfDeletion = OffsetDateTime.parse(timeOfDeletion, timeFormat);
	}

	public void setTimeOfCreation(OffsetDateTime timeOfCreation) {
		this.timeOfCreation = timeOfCreation;
	}
	
	public String getFullContent() {
		return fullContent;
	}
	
	public void setFullContent(String fullContent) {
		this.fullContent = fullContent;
	}
	
}
