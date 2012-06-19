package net.ion.isearcher.indexer.policy;

import java.io.IOException;

import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.common.MyDocument.Action;
import net.ion.isearcher.indexer.write.IWriter;

/**
 * 
 * @author bleujin
 * WritePolicy : 
 * �ش� Document�� ��� ó�������� ���� ������ ������ �����. 
 * MyDocument�� needRemoved�� True�� ������ �Ǿ� �־ ���� ����������� ���δ� WritePolicy�� �����Ѵ�. 
 * 
 */

public interface IWritePolicy {
	public void begin(IWriter writer) ;
	public Action apply(IWriter writer, MyDocument doc) throws IOException ;
	public void end(IWriter writer) ;
}
