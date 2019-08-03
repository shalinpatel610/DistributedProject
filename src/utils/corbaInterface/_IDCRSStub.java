package utils.corbaInterface;

public class _IDCRSStub extends org.omg.CORBA.portable.ObjectImpl implements utils.corbaInterface.IDCRS
{


  public boolean addCourse (String advisorId, String courseId, String semester, int capacity)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("addCourse", true);
                $out.write_string (advisorId);
                $out.write_string (courseId);
                $out.write_string (semester);
                $out.write_long (capacity);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return addCourse (advisorId, courseId, semester, capacity        );
            } finally {
                _releaseReply ($in);
            }
  } // addCourse

  public boolean removeCourse (String advisorId, String courseId, String semester)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("removeCourse", true);
                $out.write_string (advisorId);
                $out.write_string (courseId);
                $out.write_string (semester);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return removeCourse (advisorId, courseId, semester        );
            } finally {
                _releaseReply ($in);
            }
  } // removeCourse

  public org.omg.CORBA.Any listCourseAvailability (String advisorId, String semester)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("listCourseAvailability", true);
                $out.write_string (advisorId);
                $out.write_string (semester);
                $in = _invoke ($out);
                org.omg.CORBA.Any $result = $in.read_any ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return listCourseAvailability (advisorId, semester        );
            } finally {
                _releaseReply ($in);
            }
  } // listCourseAvailability


  /* Student Operations */
  public org.omg.CORBA.Any enrolCourse (String studentId, String courseId, String semester)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("enrolCourse", true);
                $out.write_string (studentId);
                $out.write_string (courseId);
                $out.write_string (semester);
                $in = _invoke ($out);
                org.omg.CORBA.Any $result = $in.read_any ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return enrolCourse (studentId, courseId, semester        );
            } finally {
                _releaseReply ($in);
            }
  } // enrolCourse

  public org.omg.CORBA.Any getClassSchedule (String studentId)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("getClassSchedule", true);
                $out.write_string (studentId);
                $in = _invoke ($out);
                org.omg.CORBA.Any $result = $in.read_any ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return getClassSchedule (studentId        );
            } finally {
                _releaseReply ($in);
            }
  } // getClassSchedule

  public boolean dropCourse (String studentId, String courseId)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("dropCourse", true);
                $out.write_string (studentId);
                $out.write_string (courseId);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return dropCourse (studentId, courseId        );
            } finally {
                _releaseReply ($in);
            }
  } // dropCourse

  public org.omg.CORBA.Any swapCourse (String studentId, String newCourseId, String oldCourseId)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("swapCourse", true);
                $out.write_string (studentId);
                $out.write_string (newCourseId);
                $out.write_string (oldCourseId);
                $in = _invoke ($out);
                org.omg.CORBA.Any $result = $in.read_any ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return swapCourse (studentId, newCourseId, oldCourseId        );
            } finally {
                _releaseReply ($in);
            }
  } // swapCourse

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:corbaInterface/IDCRS:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }

  private void readObject (java.io.ObjectInputStream s) throws java.io.IOException
  {
     String str = s.readUTF ();
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     org.omg.CORBA.Object obj = orb.string_to_object (str);
     org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();
     _set_delegate (delegate);
   } finally {
     orb.destroy() ;
   }
  }

  private void writeObject (java.io.ObjectOutputStream s) throws java.io.IOException
  {
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     String str = orb.object_to_string (this);
     s.writeUTF (str);
   } finally {
     orb.destroy() ;
   }
  }
} // class _IDCRSStub
