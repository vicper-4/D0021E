package Sim;

public class MessageBuffer {
	Message[] buffer;
	int bufferSize;
	int buffered;

	MessageBuffer(int bufferSize)
	{
		buffer = new Message[bufferSize];
		this.bufferSize = bufferSize;
		buffered = 0;
	}

	void addMsg(Message msg)
	{
		if (buffered < bufferSize)
		{
			buffer[buffered] = msg;
			buffered++;
		}
		//TODO handle full buffer
	}

	Message popMsg()
	{
		Message first = buffer[0];

		for (int i = 0; (i < buffered) && (i < bufferSize-1); i++)
		{
			buffer[i] = buffer[i+1];
		}

		buffered--;

		return first;
	}
}
