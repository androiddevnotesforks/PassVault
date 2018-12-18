package com.mrntlu.PassVault.Offline.Viewmodels;

import android.app.Application;
import android.util.Log;

import com.mrntlu.PassVault.Offline.Models.AccountsObject;
import com.mrntlu.PassVault.Offline.Models.MailObject;
import com.mrntlu.PassVault.Offline.Models.OthersObject;
import com.mrntlu.PassVault.Offline.Repositories.MailObjectRepository;
import com.mrntlu.PassVault.Offline.Repositories.OthersRepository;
import com.mrntlu.PassVault.Offline.Repositories.UserAccountsObjectRepository;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.realm.Realm;
import io.realm.RealmResults;

public class OfflineViewModel extends AndroidViewModel {

    private MutableLiveData<RealmResults<MailObject>> mMailObjects;
    private MutableLiveData<RealmResults<AccountsObject>> mUserObjects;
    private MutableLiveData<RealmResults<OthersObject>> mOtherObjects;
    private MailObjectRepository mRepo;
    private UserAccountsObjectRepository mUserRepo;
    private OthersRepository mOtherRepo;
    private Realm mRealm;

    public OfflineViewModel(@NonNull Application application) {
        super(application);
        mRealm=Realm.getDefaultInstance();
    }

    public void initMailObjects(){
        if (mMailObjects!=null){
            return;
        }
        mRepo=new MailObjectRepository(getApplication().getApplicationContext());
        mMailObjects=mRepo.getMailObjects();
    }

    public void initAccountObjects(){
        if (mUserObjects!=null){
            return;
        }
        mUserRepo=new UserAccountsObjectRepository(getApplication().getApplicationContext());
        mUserObjects=mUserRepo.getAccountsObjects();
    }

    public void initOtherObjects(){
        if (mOtherObjects!=null){
            return;
        }
        mOtherRepo=new OthersRepository(getApplication().getApplicationContext());
        //mOtherObjects=mOtherRepo.getMailObjects();
    }

    public void addMailObject(final String mail, final String password){
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                MailObject mailObject=realm.createObject(MailObject.class);
                mailObject.setMail(mail);
                mailObject.setPassword(password);
            }
        });
        RealmResults<MailObject> mailObjects=mMailObjects.getValue();
        mMailObjects.postValue(mailObjects);
    }

    public void addUserObject(final String mail, final String password,final String description){
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                AccountsObject accountsObject=realm.createObject(AccountsObject.class);
                accountsObject.setIdMail(mail);
                accountsObject.setPassword(password);
                accountsObject.setDescription(description);
            }
        });
        RealmResults<AccountsObject> accountObjects=mUserObjects.getValue();
        mUserObjects.postValue(accountObjects);
    }

    public void addOtherObject(final String description, final String password){
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                OthersObject othersObject=realm.createObject(OthersObject.class);
                othersObject.setDescription(description);
                othersObject.setPassword(password);
            }
        });
        RealmResults<OthersObject> otherObjects=mOtherObjects.getValue();
        mOtherObjects.postValue(otherObjects);
    }

    public LiveData<RealmResults<MailObject>> getMailObjects(){
        return mMailObjects;
    }

    public LiveData<RealmResults<AccountsObject>> getmUserObjects() {
        return mUserObjects;
    }

    public LiveData<RealmResults<OthersObject>> getmOtherObjects() {
        return mOtherObjects;
    }
}
