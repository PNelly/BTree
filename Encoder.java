/**
 * Encoder from String representing
 * series of DNA bases to a binary rep
 * contained in long, and vice versa
 * 
 * @author Patrick Doudy
 * CS321 Summer 2017
 */



public class Encoder {

    // Class Variables
    private static final long aBits = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
    private static final long tBits = 0b11000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
    private static final long cBits = 0b01000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
    private static final long gBits = 0b10000000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;

    private static int sequenceLength;

    // Constructor
    public Encoder(int sequenceLength){
	this.sequenceLength = sequenceLength;
    }

    // Encode String -> long
    public long encode(String sequence){
		sequence = sequence.toLowerCase();
		long output = 0;
		long t, c, g;

		for(int i=0;i<sequence.length();i++){
	    
	    	t = tBits >>> 2*i;
	    	c = cBits >>> 2*i;
	    	g = gBits >>> 2*i;

		    switch(sequence.charAt(i)){
	        case 'a':
				// ignore
				break;
			case 't':
				output = output | t;
				break;
			case 'c':
				output = output | c;
				break;
			case 'g':
				output = output | g;
				break;
			}
		}
		// shift past sign bit
		return (output >>> 2);
    }

    // Decode Long -> String
    public String decode(long sequenceBits){

	// shift back into sign bit
	sequenceBits = sequenceBits << 2;

	StringBuilder builder = new StringBuilder();

	long charBits;

       	for(int i=0; i<sequenceLength; i++){

       		// btiwise & w/ tBits give leading bits
       		charBits = sequenceBits & tBits;

		// switch only accepts int and smaller
       		if(charBits==aBits)
       		  builder.append('a');
       		if(charBits==tBits)
       		  builder.append('t');
       		if(charBits==cBits)
       		  builder.append('c');
       		if(charBits==gBits)
       		  builder.append('g');

       		sequenceBits = sequenceBits << 2;
       	}

       	return builder.toString();

    }

}