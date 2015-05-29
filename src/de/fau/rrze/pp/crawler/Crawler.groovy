/**
 * 
 * @author Johannes Lorenz, @frnktrgr, @svema, @viegelinsch
 * @see <a href="https://jolorenz.wordpress.com/2014/08/11/create-a-webcrawler-with-less-than-100-lines-of-code/">Original code</a>
 * @see <a href="https://github.com/RRZE-PP/ppcrawler">Github repository of this code</a>
 * 
 * @version 0.5.0
 */
package de.fau.rrze.pp.crawler;

@Grapes([
	@Grab(group='org.grails', module='grails-validation', version='[2.5.0,)'),
	@Grab(group='org.jsoup', module='jsoup', version='[1.8.2,)'),
	@Grab(group='log4j', module='log4j', version='[1.2.17,)')
	])


import org.grails.validation.routines.UrlValidator
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.jsoup.Jsoup

import groovy.util.logging.*
import org.apache.log4j.*
import java.util.regex.Matcher
import java.util.regex.Pattern


@Log4j
class Crawler {

	static void main(String... args) {

//		log.setLevel(Level.INFO) 
//		log.addAppender(new FileAppender(new PatternLayout("%d %5p %c{1}:%L - %m%n"), 'crawler.log'))

		def config = new ConfigSlurper().parse(new File('log4jconfig.groovy').toURL())
		PropertyConfigurator.configure(config.toProperties())

		
		def seedUrls = []
		seedUrls.add("https://www.rrze.fau.de")
//		seedUrls.add("https://www.fau.de")
		
		def crawler = new BasicWebCrawler()
		
		log.info "Start crawling ..."
		crawler.collectUrls(seedUrls)
		log.info "... finished crawling."
	}
}


@Log4j
class BasicWebCrawler {
	
	def private final boolean followExternalLinks = false
	def linksToCrawl = [] as ArrayDeque
	def visitedUrls = [] as HashSet
	def urlValidator = new UrlValidator()

	def evaluationSet = [] as Set

	def final static Pattern IGNORE_SUFFIX_PATTERN = Pattern.compile("((.*(\\.(shtml?|html?|php|css|js|bmp|gif|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|pdf|rm|smil|wmv|swf|wma|docx?|pptx?|xlsx?|od[a-z]|ot[a-z]))(\\?|#)?.*)|(.*(\\?|#){1}.*)\$)")
	//Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"+ "|png|tiff?|mid|mp2|mp3|mp4" + "|wav|avi|mov|mpeg|ram|m4v|pdf" +"|rm|smil|wmv|swf|wma|zip|rar|gz))\$")

	def private final timeout = 3000
	def private final userAgent = "Mozilla"

	def collectUrls(List seedURLs) {
		
//		log.setLevel(Level.DEBUG)
//		log.addAppender(new FileAppender(new PatternLayout("%d %5p %c{1}:%L - %m%n"), 'crawler.log'))
		
		seedURLs.each {url ->
			linksToCrawl.add(url);
		}
		try {
			def i = 0
			while(!linksToCrawl.isEmpty()){
				if (++i % 10 == 0) {
					println "$i: ${linksToCrawl.size()}"
				}

				def urlToCrawl = linksToCrawl.poll() as String // "poll" removes and returns the first url in the"queue"
				try {
					visitedUrls.add(urlToCrawl)
					// extract URL from HTML using Jsoup
					def doc = Jsoup.connect(urlToCrawl).userAgent(userAgent).timeout(timeout).get() as Document
					def links = doc.select("a[href]") as Elements
					links.each {Element link ->
						// find absolute path
						def absHref = link.attr("abs:href") as String
						if (shouldVisit(absHref, seedURLs)) {
							// If this set already contains the element, the call leaves the set unchanged and returns false.
							if(visitedUrls.add(absHref)){
								if (!linksToCrawl.contains(absHref)) {
									linksToCrawl.add(absHref)
									log.debug "new link ${absHref} added to queue"
								}
							}
						}
						if (shouldEvaluate(absHref, seedURLs)) {
							evaluationSet.add(absHref)
						}
					}
				} catch (org.jsoup.HttpStatusException e) {
					// TODO: ignore 404
					// handle exception
					log.error "$e"
				} catch (java.net.SocketTimeoutException e) {
					// handle exception
					log.error "$e"
				} catch (IOException e) {
					// handle exception
					log.error "$e"
				}
			}
			
			log.info "URL to evaluate:"
			evaluationSet.each{
				log.info "${it}"
			}

		} catch (Exception e){
			// handle exception
			log.error "$e"
		}
	}

	def private boolean shouldVisit(String url, def seedURLs) {
		// filter out invalid links
		def visitUrl = false
		
//		log.setLevel(Level.INFO)
//		log.addAppender(new FileAppender(new PatternLayout("%d %5p %c{1}:%L - %m%n"), 'crawler.log'))
		
		try {
			def boolean followUrl = false
			def matchIgnore = IGNORE_SUFFIX_PATTERN.matcher(url) as Matcher
			def isUrlValid = urlValidator.isValid(url)

			if (followExternalLinks == false) {
				// follow only urls which starts with any of the seed urls
				followUrl = seedURLs.any { seedUrl ->
					if (url.startsWith(seedUrl)) {
						return true // break
					}
				}
			} else {
				// follow any url
				followUrl = true
			}

			// visit url only is it not contains unwanted file endings, if it is valid and if it is in our scope
			visitUrl = (!matchIgnore.matches() && isUrlValid && followUrl)

		} catch (Exception e) {
			// handle exception
			log.error "$e"
		}
		return visitUrl
	}

	def private boolean shouldEvaluate(String url, def seedURLs) {

//		log.setLevel(Level.INFO)
//		log.addAppender(new FileAppender(new PatternLayout("%d %5p %c{1}:%L - %m%n"), 'crawler.log'))
		
		try {
			def boolean followUrl = false
			def matchIgnore = IGNORE_SUFFIX_PATTERN.matcher(url) as Matcher

			//			sort out external url
			if (followExternalLinks == false) {
				// follow only urls which starts with any of the seed urls
				followUrl = seedURLs.any { seedUrl ->
					if (url.startsWith(seedUrl)) {
						return true // break
					}
				}
			} else {
				// follow all url
				followUrl = true
			}

			//			exclude url which end on a trailing slash
			def someUrl = ( url ==~ /.*\//)

			//			url contains no trailing slash, it does not contain a file we want to ignore, we want to follow it, url is not just another directory -> so true
			return (!someUrl && !matchIgnore.matches() && followUrl && url.tokenize("\\/")[-1].indexOf(".")>0)

		} catch (Exception e) {
			// handle exception
			log.debug "$e - shouldEvaluateException"
		}
	}
}


