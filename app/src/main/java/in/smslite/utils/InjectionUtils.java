package in.smslite.utils;

import in.smslite.contacts.Contact;

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
