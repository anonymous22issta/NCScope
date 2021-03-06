public class TraceErrorRecord extends TraceMessageRecord {

	private TraceCorruptedException mTraceCorruptedException;
    
    public TraceErrorRecord(TraceCorruptedException tce) {
        super(tce.getShortMessage());
        mTraceCorruptedException = tce;
    }
    
    @Override
    public MessageType getMessageType() {
        return MessageType.ERROR;
    }
	
    public String getErrorDetails() {
        return mTraceCorruptedException.getShortMessage();
    }
    
}
