package de.bsd.x2svg.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

/**
 * A utility class that handles closing of I/O streams.
 * 
 * @author gfloodgate
 * @since 1.1
 */
public abstract class IOUtil
{
    
    /**
     * Handle closing of an <code>InputStream<code> instance. 
     * 
     * @param in The <code>InputStream<code> instance to close.
     */
    public static void close(final InputStream in)
    {
        if (in != null)
        {
            try
            {
                in.close();
            }
            catch (IOException ie)
            {
                // No much that can be done, just log the failure.
                System.err.println("Unable to close InputStream - " + ie.getMessage());
            }
        }
    }

    
    /**
     * Handle closing of an <code>OutputStream<code> instance. 
     * 
     * @param out The <code>OutputStream<code> instance to close.
     */
    public static void close(final OutputStream out)
    {
        if (out != null)
        {
            try
            {
                out.close();
            }
            catch (IOException ie)
            {
                // No much that can be done, just log the failure.
                System.err.println("Unable to close OutputStream - " + ie.getMessage());
            }
        }
    }


    /**
     * Handle closing of a <code>Writer<code> instance. 
     * 
     * @param out The <code>Writer<code> instance to close.
     */
    public static void close(final Writer out)
    {
        if (out != null)
        {
            try
            {
                out.close();
            }
            catch (IOException ie)
            {
                // No much that can be done, just log the failure.
                System.err.println("Unable to close Writer - " + ie.getMessage());
            }
        }
    }

}
