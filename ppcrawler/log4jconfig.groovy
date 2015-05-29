/**
 * 
 * @see <a href="https://gist.github.com/neversleepz/7381062#file-loggingfromconsolescript-groovy">neversleepz Gist</a>
 * @see <a href="https://stackoverflow.com/questions/19868180/groovy-script-and-log4j">stackoverflow</a>
 *
 */

log4j {
	
		   appender.stdout = "org.apache.log4j.ConsoleAppender"
		   appender."stdout.layout"="org.apache.log4j.PatternLayout"
		   appender."stdout.layout.ConversionPattern"="%d %5p %c{1}:%L - %m%n"
		   
		   appender.scrlog = "org.apache.log4j.DailyRollingFileAppender"
		   appender."scrlog.DatePattern"="'.'yyyy-MM-dd"
		   appender."scrlog.Append"="true"
		   
//		   appender.scrlog = "org.apache.log4j.FileAppender"
		   appender."scrlog.layout"="org.apache.log4j.PatternLayout"
		   appender."scrlog.layout.ConversionPattern"="%d %5p %c{1}:%L - %m%n"
		   appender."scrlog.file"="crawler.log"
		
		   rootLogger="info,scrlog,stdout"
		
}
