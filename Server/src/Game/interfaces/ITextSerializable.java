package Game.interfaces;

public interface ITextSerializable {
	
	public String serialize();
	
	public int deserialize(String rawText, int offset);
	
}