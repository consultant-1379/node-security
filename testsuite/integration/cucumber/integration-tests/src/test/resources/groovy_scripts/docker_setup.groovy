/**
 * Docker Setup
 * 
 * This is script is responsible to setup docker env
 * 
 * 
 * @author ebialan
 *         
 */


import java.text.SimpleDateFormat;

class Logger {
    def logFile = System.out;
    private final static String dateFormat = "[dd.MM.yyyy;HH:mm:ss.SSS]"

    public Logger () {
    }

    public Logger (String logFilePath) {
        logFile = new File(logFilePath)
    }

    def methodMissing(String name, args) {
        def message = args[0]
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat)
        String date = formatter.format(new Date())
        
        switch (name.toLowerCase()) {
            case 'console':
               logFile << "${date} ${message}\n"
            break
        }
    }
}


/**
 * Main
 */
dockerComposeDirPath = args[0]
loggerFilePath = args.length > 1 ? args[1] : null

logger = loggerFilePath ? new Logger(loggerFilePath) : new Logger()
workDir = new File(dockerComposeDirPath)

runBashCmd("pwd", workDir)
runBashCmd("docker-compose ps", workDir)
runBashCmd("./docker-stop.sh", workDir)
//runBashCmd("docker-compose kill", workDir)
//runBashCmd("./docker-start.sh --no-pull --no-recreate", workDir, false)

runBashCmd("./docker-netsim-start.sh", workDir, false)


def runBashCmd(cmd, dir){
    return runBashCmd(cmd, dir, true)
}

def runBashCmd(String cmd, File dir, boolean wait){
    def logMessage = ">>>> ${cmd}"
    def stdoutStream = loggerFilePath ? new FileOutputStream(loggerFilePath) : System.out
    def stderrStream = loggerFilePath ? new FileOutputStream(loggerFilePath) : System.err

    logger.console ''
    logger.console logMessage

    def proc =  (["/bin/bash", "-c", cmd] as String[]).execute(null, dir)
    
    if(wait){
        proc.waitForProcessOutput(stdoutStream, stderrStream)
    } else {
        proc.consumeProcessOutput(stdoutStream, stderrStream)
    }
    return proc
}

def throwException(message) {
    writeEnvVariables(message)
    dumpResult()
    throw new InterruptedException(message)
}

