package com.mrntlu.PassVault.Offline.Repositories;

import android.content.Context;
import com.mrntlu.PassVault.Offline.Models.AccountsObject;
import androidx.lifecycle.MutableLiveData;
import io.realm.Realm;
import io.realm.RealmResults;

public class UserAccountsObjectRepository {

    private RealmResults<AccountsObject> dataSet;
    private Context context;
    private Realm mRealm;

    public UserAccountsObjectRepository(Context context) {
        this.context = context;
        mRealm=Realm.getDefaultInstance();
        dataSet=mRealm.where(AccountsObject.class).findAll();
    }

    public MutableLiveData<RealmResults<AccountsObject>> getAccountsObjects(){
        closeRealm();
        MutableLiveData<RealmResults<AccountsObject>> data=new MutableLiveData<>();
        data.setValue(dataSet);
        return data;
    }

    private void closeRealm(){
        if (mRealm!=null && !mRealm.isClosed()){
            mRealm.close();
        }
    }

}
