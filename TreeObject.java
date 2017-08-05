
public class TreeObject {

	private Long key;
	private int  frequency;

    public TreeObject(Long key, int frequency) {
        this.key = key;
        this.frequency = frequency;
    }

	public TreeObject(Long key){
        this.key = key;
        frequency = 0;
    }
	
	public TreeObject(long key){
		this.key = new Long(key);
		frequency = 0;
	}

    public void setKey(long key) {
        this.key = key;
    }
    
    public Long getKey() {
        return key;
    }
    
    public void incrementFrequency(){
    	frequency++;
    }
    
    public int getFrequency(){
    	return frequency;
    }

}
