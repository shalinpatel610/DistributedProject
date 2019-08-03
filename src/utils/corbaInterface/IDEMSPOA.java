package utils.corbaInterface;


public abstract class IDEMSPOA extends org.omg.PortableServer.Servant
 implements utils.corbaInterface.IDEMSOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put("addEvent", new java.lang.Integer(0));
    _methods.put("removeEvent", new java.lang.Integer(1));
    _methods.put("listEventAvailability", new java.lang.Integer(2));
    _methods.put("bookEvent", new java.lang.Integer(3));
    _methods.put("getBookingSchedule", new java.lang.Integer(4));
    _methods.put("cancelEvent", new java.lang.Integer(5));
    _methods.put("swapEvent", new java.lang.Integer(6));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  
       {
         String managerId = in.read_string ();
         String eventId = in.read_string ();
         String eventType = in.read_string ();
         int capacity = in.read_long ();
         boolean $result = false;
         $result = this.addEvent (managerId, eventId, eventType, capacity);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 1:  // corbaInterface/IDEMS/cancelEvent
       {
         String managerId = in.read_string ();
         String eventId = in.read_string ();
         String eventType = in.read_string ();
         boolean $result = false;
         $result = this.removeEvent (managerId, eventId, eventType);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 2:  
       {
         String managerId = in.read_string ();
         String eventType = in.read_string ();
         org.omg.CORBA.Any $result = null;
         $result = this.listEventAvailability (managerId, eventType);
         out = $rh.createReply();
         out.write_any ($result);
         break;
       }


  /* Student Operations */
       case 3:  
       {
         String customerId = in.read_string ();
         String eventId = in.read_string ();
         String eventType = in.read_string ();
         org.omg.CORBA.Any $result = null;
         $result = this.bookEvent (customerId, eventId, eventType);
         out = $rh.createReply();
         out.write_any ($result);
         break;
       }

       case 4:
       {
         String customerId = in.read_string ();
         org.omg.CORBA.Any $result = null;
         $result = this.getBookingSchedule (customerId);
         out = $rh.createReply();
         out.write_any ($result);
         break;
       }

       case 5:  
       {
         String customerId = in.read_string ();
         String eventId = in.read_string ();
         boolean $result = false;
         $result = this.cancelEvent (customerId, eventId, eventType);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 6:  
       {
         String customerId = in.read_string ();
         String neweventId = in.read_string ();
         String oldeventId = in.read_string ();
         org.omg.CORBA.Any $result = null;
         $result = this.swapEvent (customerId, neweventId, oldeventId, newEventType, oldEventType));
         out = $rh.createReply();
         out.write_any ($result);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:corbaInterface/IDEMS:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public IDEMS _this() 
  {
    return IDEMSHelper.narrow(
    super._this_object());
  }

  public IDEMS _this(org.omg.CORBA.ORB orb) 
  {
    return IDEMSHelper.narrow(
    super._this_object(orb));
  }


} // class IDEMSPOA
