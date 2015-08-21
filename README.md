README v1.0 / 2015-08-17

# Crawler

## Introduction

We needed a crawler to find files on our website, which are not of the "usual pictures, slides and office" file types. Everything that is not "usual" and might create "problems", should be found and put on a list for review. This is exactly what this crawler does.

Special thanks to [Johannes Lorenz](https://jolorenz.wordpress.com/2014/08/11/create-a-webcrawler-with-less-than-100-lines-of-code/ "Link to original code") for allowing to reuse his code.

## Usage

```groovy
crawler$ groovy src/de/fau/rrze/pp/crawler/Crawler.groovy
```

## Contributing

Issue a pull request. It will be evaluated and in all likelihood merged.

## Help

Currently there is no help beside of knowledge and understanding ... ☹

## Installation

### Requirements

* [git](https://git-scm.com/)
* [Groovy Programming Language](http://www.groovy-lang.org/)

### Clone this repository

```bash
git clone https://github.com/RRZE-PP/crawler.git
```


### Configuration

Change the content of the list ```seedUrls.add("")``` in ```src/de/fau/rrze/pp/crawler/Crawler.groovy``` (starting at line 43).

Pay attention to use proper URLs!


## Credits

* Thanks to Johannes Lorenz for the [original code](https://jolorenz.wordpress.com/2014/08/11/create-a-webcrawler-with-less-than-100-lines-of-code/) and the allowance for publishing
* This template was taken from https://opensource.com/business/15/6/template-starting-project-documentation

## Contact

* hendrik.eggers@fau.de
* https://github.com/RRZE-PP

## License

This project is licensed under GNU GPL V 3. See [LICENSE](LICENSE.md "LICENSE file") for details.
