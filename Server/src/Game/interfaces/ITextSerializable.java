package Game.interfaces;

public interface ITextSerializable {
	
	String serialize();
	
	int deserialize(String rawText, int offset);
	
}