# About
Winlet is a Java web application framework for building Rich Internet Application that composed of Winlet windows. A Winlet window is similar to a web widget, it can be added to any web page (even a static web page on a different domain), occupies only a portion of the web page, communicates with server using Ajax. Multiple Winlet windows can coexit and collaborate on one page. A Winlet window can be included by another web window at server side, to create sophisticated functionality. A Winlet window can also be added into web page dynamically by another Winlet window at client side.

Winlet framework is built on top of Spring MVC, Hibernate, jQuery and Bootstrap.

# Use Winlet

Latest Winlet java library has been published to Maven central repository. Add below lines to pom.xml to include Winlet library into your project:

```
<dependency>
  <groupId>com.aggrepoint.framework</groupId>
  <artifactId>winlet</artifactId>
  <version>0.1.10</version>
</dependency>
```
Winlet requires JRE 8.

# Documents
[Winlet Document](http://docs.aggrepoint.com)
