package Model;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Base64;


import com.sinapp.sharathsind.tradepost.Constants;
import com.sinapp.sharathsind.tradepost.UserLocation;
import com.sinapp.sharathsind.tradepost.userdata;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.util.ArrayList;
import java.util.Vector;


import webservices.MainWebService;

/**
 * Created by sharathsind on 2015-06-01.
 */
public class RegisterWebService {
    private static final String SOAP_ACTION = "http://webser/Register/operationRequest";
    private static final String METHOD_NAME = "operation";
    private static final String NAMESPACE = "http://webser/";
    private static final String URL ="http://services.tradepost.me:8084/TDserverWeb/Register?wsdl";
private  static final String mname="gcmwebservice";
    private static final String SOAP_ACTION1 = "http://webser/Register/gcmwebserviceRequest";
    public static ContentValues signUp(String username, String email, String s, String fb, Bitmap profilepic, boolean b,SQLiteDatabase db) {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("username",username);
        request.addProperty("email",email);
        request.addProperty("password",s);
        request.addProperty("type",fb);
        request.addProperty("os","android");
        request.addProperty("confirm",String.valueOf(b));
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        profilepic.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte [] b1=baos.toByteArray();
        ContentValues cv=new ContentValues();
        String temp= Base64.encodeToString(b1, Base64.DEFAULT);
        request.addProperty("imagedata", temp);
        request.addProperty("api", Constants.GCM_Key);


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.setOutputSoapObject(request);

        HttpTransportSE ht = new HttpTransportSE(URL,10000000);
        try {
            ht.call(SOAP_ACTION, envelope);
            SoapPrimitive response = (SoapPrimitive)envelope.getResponse();
            String res=response.getValue().toString();
            cv.put("username",username);
            cv.put("email",email);
            cv.put("emailconfirm",String.valueOf(b));
            cv.put("itype",fb);
            cv.put("userid",res);
            Constants.userid =Integer.parseInt(res);
            userdata.userid = Integer.parseInt(res);
            cv.put("password",s);
            cv.put("profilepicture", "lib/profile.png");
            request=new SoapObject(NAMESPACE,mname);
            request.addProperty("os", "android");
            request.addProperty("userid", Integer.parseInt(res));
            request.addProperty("apikey",Constants.GCM_Key);
            SoapPrimitive s1= MainWebService.getretryMsg(request, URL, SOAP_ACTION1, 0);
            String sj="h";
            getItems();
       //     getnot();
    //  long l=   db.insert("login",null,cv);
//long k=l;
        }
catch (EOFException e)
{
signUp(username, email, s, fb, profilepic, b, db);
}
        catch (Exception e) {
            e.printStackTrace();
        }

        return cv;
    }
   public  static void getItems()
   {try {
       SoapObject object = new SoapObject("http://webser/", "getuseritems");
       //SoapObject object = new SoapObject("http://webser/", "getuseritems");
       object.addProperty("userid", userdata.userid);
       Vector object1 = MainWebService.getMsg1(object, "http://services.tradepost.me:8084/TDserverWeb/Search?wsdl", "http://webser/Search/getuseritemsRequest");
       userdata.items = new ArrayList<Integer>();


       if (object1 != null) {
           for (Object i : object1) {
               userdata.items.add(Integer.parseInt(((SoapPrimitive) i).getValue().toString()));
           }
       }
       userdata.i = new ArrayList<ItemResult>();

       for (int i : userdata.items) {

           SoapObject obje = new SoapObject("http://webser/", "getItembyId");
           obje.addProperty("itemid", i);
           KvmSerializable result1 = MainWebService.getMsg2(obje, "http://services.tradepost.me:8084/TDserverWeb/GetItems?wsdl"
                   , "http://webser/GetItems/getItembyIdRequest");

           ItemResult ir = new ItemResult();
           ir.item = new Item();

           SoapObject object12 = (SoapObject) result1.getProperty(0);
           //for(int u=0;u<object.getPropertyCount())
           ir.item.set(object12);
           //SoapObject o7=(SoapObject)result1;
           //Object j=       o.getProperty("images");
           int i1 = result1.getPropertyCount();
           ir.images = new String[i1 - 1];

           for (int u1 = 1; u1 < i1; u1++) {
               ir.images[u1 - 1] = result1.getProperty(u1).toString();

           }
           obje = new SoapObject("http://webser/", "searchbyint");
           obje.addProperty("name", i);
           Vector result2 = MainWebService.getMsg1(obje, "http://services.tradepost.me:8084/TDserverWeb/NewWebService?wsdl"
                   , "http://webser/NewWebService/searchbyintRequest");
           if (result2 != null) {

               int index = 0;
               ir.tags = new String[result2.size()];

               for (Object o : result2) {
                   ir.tags[index] = ((SoapPrimitive) o).getValue().toString();
                   index++;

               }
           }

           userdata.i.add(ir);


       }




   }
   catch (Exception e)
   {

   }
   }




    private static final String SOAP_ACTION3 = "http://webser/Register/additemsRequest";
    private static final String METHOD_NAME3 = "additems";
   // private static final String NAMESPACE = "http://webser/";
    //private static final String URL ="http://192.168.43.248:8084/TDserverWeb/AddItems?wsdl";
    public static SoapPrimitive sendDataToServer(String itemTitle, String descrpition, String[] tags, Object[] images, int condition, int userid, String category,Context context) {

        SoapObject object = new SoapObject(NAMESPACE, METHOD_NAME3);
        object.addProperty("itemname", itemTitle);
        object.addProperty("desc",descrpition);
        if(userdata.mylocation==null)
        {

            SharedPreferences prefs = context.getSharedPreferences("loctradepost", context.MODE_PRIVATE);

            userdata.mylocation=new UserLocation();
            float restoredText = prefs.getFloat("lat", 0);
            float restoredText1 = prefs.getFloat("long", 0);
            String restoredText2 = prefs.getString("city", null);
            if(restoredText2!=null) {
                userdata.mylocation = new UserLocation();
                userdata.mylocation.city=restoredText2;
                userdata.mylocation.latitude=restoredText;
                userdata.mylocation.Longitude=restoredText1;
            }
        }
        object.addProperty("latitude", String.format("%.2f",userdata.mylocation.latitude));


        //object.addProperty("tags",tag);
        object.addProperty("longi", String.format("%.2f",userdata.mylocation.Longitude));
        object.addProperty("userid", userdata.userid);
        object.addProperty("category", category);
        object.addProperty("condition",condition);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        ArrayList<HeaderProperty> headerPropertyArrayList = new ArrayList<HeaderProperty>();
        headerPropertyArrayList.add(new HeaderProperty("Connection", "close"));
        envelope.setOutputSoapObject(object);
   //    MarshalFloat m=new MarshalFloat();
     //   m.register(envelope);
       // new MarshalBase64().register(envelope);
        System.setProperty("http.keepAlive", "false");
        HttpTransportSE ht = new HttpTransportSE( URL,50000000);

        ht.debug=true;
        try {


            ht.call(SOAP_ACTION3, envelope,headerPropertyArrayList);
            ht.getServiceConnection().setRequestProperty("connection","close");
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            //  String res = response.ge().toString();
            return response;
        }
catch (EOFException ex)
{
    return  sendDataToServer(itemTitle,descrpition,tags,images,condition,userid,category,context);

}
        catch (Exception e) {
            e.printStackTrace();
        }


        return null;

    }
    public static SoapPrimitive sendDataEdit(String itemTitle, String descrpition, String[] tags, Object[] images, int condition, int userid, String category,int item) {

        SoapObject object = new SoapObject(NAMESPACE,"saveitem" );
        object.addProperty("itemname", itemTitle);
        object.addProperty("itemid", item);
        object.addProperty("desc",descrpition);
        object.addProperty("latitude", String.format("%.2f",userdata.mylocation.latitude));


        //object.addProperty("tags",tag);
        object.addProperty("longi", String.format("%.2f",userdata.mylocation.Longitude));
        object.addProperty("userid", userdata.userid);
        object.addProperty("category", category);
        object.addProperty("condition",condition);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        ArrayList<HeaderProperty> headerPropertyArrayList = new ArrayList<HeaderProperty>();
        headerPropertyArrayList.add(new HeaderProperty("Connection", "close"));
        envelope.setOutputSoapObject(object);
        //    MarshalFloat m=new MarshalFloat();
        //   m.register(envelope);
        // new MarshalBase64().register(envelope);
        System.setProperty("http.keepAlive", "false");
        HttpTransportSE ht = new HttpTransportSE( "http://services.tradepost.me:8084/TDserverWeb/EditdeleteItem?wsdl",50000000);

        ht.debug=true;
        try {


            ht.call("http://webser/EditdeleteItem/saveitemRequest", envelope,headerPropertyArrayList);
            ht.getServiceConnection().setRequestProperty("connection","close");
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            //  String res = response.ge().toString();
            return response;
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        return null;

    }


}
