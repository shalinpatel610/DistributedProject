package utils.corbaInterface;


public final class IDEMSHolder implements org.omg.CORBA.portable.Streamable
{
  public utils.corbaInterface.IDEMS value = null;

  public IDEMSHolder ()
  {
  }

  public IDEMSHolder (utils.corbaInterface.IDEMS initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = utils.corbaInterface.IDEMSHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
	  utils.corbaInterface.IDEMSHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return utils.corbaInterface.IDEMSHelper.type ();
  }

}
