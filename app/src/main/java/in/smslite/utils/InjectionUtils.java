package in.smslite.utils;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;

import in.smslite.contacts.Contact;
import in.smslite.viewModel.LocalMessageDbViewModel;

/**
 * Encapsulate creation of various objects(Provide static method to inject various classes needed for Inboxy)
 *
 * Created by rahul1993 on 5/21/2018.
 */

public class InjectionUtils {

  public static Contact InjectContact(){
    return new Contact();
  }

//  public static LocalMessageDbViewModel injectLocalMessageDbViewModel(Context context){
//  }
}
