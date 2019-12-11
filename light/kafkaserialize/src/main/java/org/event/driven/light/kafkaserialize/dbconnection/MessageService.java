package org.event.driven.light.kafkaserialize.dbconnection;

import org.event.driven.light.kafkaserialize.common.LocalMessage;

import java.sql.ResultSet;
import java.sql.Timestamp;

public class MessageService {

    private DBHelper dbHelper;

    public MessageService(DBHelper dbHelper){
        this.dbHelper = dbHelper;
    }

    public synchronized void saveLocalMessage(LocalMessage localMessage){
        String sql="insert into localmessage (globalId, parentId, localId, startTime, " +
                "expireTime, messageState, transactionState, compensateMethod, payloads, approveMethod) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

        try{
            dbHelper.execute(sql);
            dbHelper.pst.setString(1, localMessage.getGlobalId());
            dbHelper.pst.setString(2, localMessage.getParentId());
            dbHelper.pst.setString(3, localMessage.getLocalId());
            dbHelper.pst.setTimestamp(4, localMessage.getStartTime());
            dbHelper.pst.setTimestamp(5, localMessage.getExpireTime());
            dbHelper.pst.setInt(6, localMessage.getMessageState());
            dbHelper.pst.setInt(7, localMessage.getTransactionState());
            dbHelper.pst.setString(8, localMessage.getCompensateMethod());
            dbHelper.pst.setObject(9, localMessage.getPayloads());
            dbHelper.pst.setString(10, localMessage.getApproveMethod());

            dbHelper.pst.addBatch();
            dbHelper.pst.executeBatch();
        }catch(Exception e){
            System.out.println("Error in inserting values to LocalMessage Table. globalId: "+localMessage.getGlobalId());
            e.printStackTrace();
        }
    }

    public LocalMessage getReflectMethod(String globalId){
        String sql="select * from localmessage where globalId=?";
        LocalMessage localMessage = null;

        try{
            dbHelper.execute(sql);
            dbHelper.pst.setString(1, globalId);

            ResultSet rs=dbHelper.pst.executeQuery();
            while(rs.next()){
                String parentId=rs.getString(3);
                String localId=rs.getString(4);
                Timestamp startTime=rs.getTimestamp(5);
                Timestamp expireTime=rs.getTimestamp(6);
                int messageState=rs.getInt(7);
                int transactionState=rs.getInt(8);
                String compensateMethod=rs.getString(9);
                byte[] payloads=rs.getBytes(10);
                String approveMethod=rs.getString(11);

                localMessage = new LocalMessage( globalId,parentId, localId, startTime, expireTime, messageState,
                        transactionState, compensateMethod, approveMethod, payloads);
            }

        }catch(Exception e){
            System.out.println("Error in select approve method from mysql");
        }

        return localMessage;
    }

    public void updateMessageState(int messageState, String globalId, String localId){
        String sql="update localmessage set messageState=? where globalId=? and localId=?";

        try{
            dbHelper.execute(sql);
            dbHelper.pst.setInt(1,messageState);
            dbHelper.pst.setString(2, globalId);
            dbHelper.pst.setString(3, localId);

            dbHelper.pst.executeUpdate();
        }catch(Exception e){
            System.out.println("Error in update messageState in LocalMessage");
            e.printStackTrace();
        }
    }

    public void updateLockKey(String globalId, String lockKey){
        String sql="update localmessage set lockKey=? where globalId=?";

        try{
            dbHelper.execute(sql);
            dbHelper.pst.setString(1,lockKey);
            dbHelper.pst.setString(2, globalId);

            dbHelper.pst.executeUpdate();
        }catch(Exception e){
            System.out.println("Error in update lockKey in LocalMessage");
            e.printStackTrace();
        }
    }
}
