package com.example.weixinapplication;


import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.app.Fragment;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class contactFragment extends Fragment {

    private RecyclerView recyclerView;
    //private List<String> mList = new ArrayList<>();
    private List<Map<String,String>>data=new ArrayList<Map<String, String>>();
    private HashMap<String, String> item;
    private Context context;
    private adapter_expand adapter;
    private View view;

    public contactFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.tab03, container, false);

        recyclerView=view.findViewById(R.id.rcv_expandcollapse);
        context=this.getActivity();
        initexpandData();
        adapter=new adapter_expand(context,data);

        LinearLayoutManager manager=new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults[0]!= PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(view.getContext(), "未授权，无法实现预定的功能！", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
        }
    }
    private void initexpandData(){
        Uri uri= ContactsContract.Contacts.CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor=resolver.query(uri, null, null, null, null);  //得到记录集
        if(cursor!=null){
            while(cursor.moveToNext()){
                //先获取联系人_id字段的索引号后再获取_id值
                int idFieldIndex=cursor.getColumnIndex("_id");
                int id=cursor.getInt(idFieldIndex);

                //先获取联系人姓名字段的索引号后再获取姓名字段值
                int nameFieldIndex  = cursor.getColumnIndex("display_name");
                String name=cursor.getString(nameFieldIndex);

                int numCountFieldIndex=cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
                int numCount=cursor.getInt(numCountFieldIndex);   //获取联系人的电话号码个数
                String phoneNumber="";
                if(numCount>0){       //联系人有至少一个电话号码
                    //在类ContactsContract.CommonDataKinds.Phone中根据id查询相应联系人的所有电话；
                    Cursor phonecursor=getActivity().getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"=?",
                            new String[]{Integer.toString(id)}, null);
                    if(phonecursor!=null){
                        if(phonecursor.moveToFirst()){     //仅读取第一个电话号码
                            int numFieldIndex=phonecursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                            phoneNumber=phonecursor.getString(numFieldIndex);
                        }
                        phonecursor.close();
                    }
                }
                item=new HashMap<String,String>();  //必须循环创建
                item.put("name", name);
                item.put("phoneNumber", phoneNumber);
                data.add(item);
            }
            cursor.close();
        }
    }

}
