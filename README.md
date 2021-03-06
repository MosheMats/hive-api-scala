[![Build Status](https://travis-ci.org/wix/hive-api-scala.svg?branch=master)](https://travis-ci.org/wix/hive-api-scala)

# wix-hive-scala
==============

Scala client for the Wix Hive API

## Prerequisites

- **Read about** [developing a third party app for the Wix platform](http://dev.wix.com/docs/display/DRAF/Third+Party+Apps+-+Introduction)
- **Register your app** [here](http://dev.wix.com/docs/display/DRAF/Dev+Center+Registration+Guide)
to **obtain** your **APP_KEY** and **APP_SECRET**
- [Maven](http://maven.apache.org/)
- [Scala 2.11.5](http://www.scala-lang.org/download/2.11.5.html)

## Installation

### Maven

``` maven
<dependency>
    <groupId>com.wix.hive</groupId>
    <artifactId>hive-api-scala</artifactId>
    <version>1.3.0-SNAPSHOT</version>
</dependency>
```

### SBT

``` sbt
libraryDependencies += "com.wix.hive" %% "hive-api-scala" % "1.3.0-SNAPSHOT"
```

## Quick Start

### Configuration

The entry point to the Wix Hive API is the `HiveClient` class. You can initialize
the class using a configuration file or by passing it the configuration values.

If you use the HiveClient() apply method you'll get the configurations from the
configurations file ('reference.conf'). You can override any parameter by passing
it to the apply method or using the constructor.

####The configuration file looks like:

``` scala
hive-client {
  credentials {
    appId = "your app-id here"
    appSecret = "your secret-key here"
  }
  baseUrl = "https://openapi.wix.com"
}
```

1. The `hive-client.credentials.appSecret` and `hive-client.credentials.appId`
are obtained by registering an app as it is outlined
[here](http://dev.wix.com/docs/display/DRAF/Dev+Center+Registration+Guide)
2. The `instance` is obtained by decoding the signed app instance. Learn more
about this  [here](http://dev.wix.com/docs/display/DRAF/Using+the+Signed+App+Instance)

Sample code to retrieve contact by ID

``` scala
  val client = new HiveClient()
  client.execute(instance, GetContactById(contactId))
```

As a parameter you can use any class inherits from `HiveBaseCommand`

There's an alternative way which is easier if you need to execute multiple commands on
the same instance. You can use the convenience method 'executeForInstance'

``` scala
  val executor = client.executeForInstance(instance)
  executor(GetContactById(contactId))
```

#### Spring integration

If you're using [Spring Framework](http://projects.spring.io/spring-framework/),
you can mixin the `WixInstanceDecoderSupport` in your spring config to easily access
the instance from within your controllers:

``` scala
import com.wix.hive.server.resolvers.springframework.WixInstanceDecoderSupport

@Configuration
class MySpringConfig extends WixInstanceDecoderSupport {

  // App secret key used to verify signed instance
  override def wixInstanceSecretKey: String = '<secret key>' 

  // Optional: HTTP header name in which the signed instance will be passed
  override def wixInstanceHeaderName: String = '<header name>' // Default: 'X-Wix-Instance'
    
  // Optional: query param name in which the signed instance will be passed
  override def wixInstanceQueryParamName: String = '<param name>' // Default: 'instance'
  
  // Rest of your config

}
```

Then you can access the instance in your controllers like so:

``` scala
@Controller
class MyController {

  @RequestMapping(value = Array("/my-endpoint"))
  def myEndpoint(instance: WixInstance) = {
    // ...
  }

}
```

### Hive Errors

#### Response Errors

``` scala
    400 -> "Bad Request",
    403 -> "Forbidden",
    404 -> "NotFound",
    408 -> "Request timeout",
    429 -> "Too many requests",
    500 -> "Internal server error",
    502 -> "Bad gateway",
    503 -> "Service unavailable",
    504 -> "Gateway timeout"
```

#### Concurrency Control

The contacts add and update methods have a concurrency control mechanism associated
with them. The mechanism is based on the ``modifiedAt`` request parameter. This
parameter needs to have the same value as the underlying contact that is being updated.
For example: let us assume we have a contact with ``id=1`` and
``modifiedAt=2014-10-01T14:43:48.560+03:00`` and we want to update the email field.
So lets think about the concurrency now. Let assume we have two update email requests
that come in the same time and they get processed sequentially.
First one would get processed and update the contact email and in the same time the
contacts’ ``modifiedAt`` will change.
Second request gets processed but it will fail with a concurrency validation error
because it is trying to perform an update operation on a old version of the contact
object.
And the system knows that by comparing the two ``modifiedAt`` parameters (one from
the DB and the one provided).

## Contributing

**Everyone** is encouraged to help **improve** this library.
Some of the ways you can contribute include:

1. Use alpha, beta, and pre-release versions.
2. Report bugs.
3. Suggest new features.
4. Write or edit documentation.
5. Write specifications.
6. Write code (**no patch is too small**: fix typos, clean up inconsistent whitespace).
7. Refactor code.
8. Fix [issues](https://github.com/wix/wix-hive-scala/issues).
9. Submit an Issue

### Using the test-kit

The test-kit provides you with the ability to test your code end to end.

The testkit allows you to set up embedded HTTP server that simulates 
the real hive server. Here's a code sample that independent of testing framework.

A sample of
- Hive client test : com.wix.hive.commands.SitesIT
- Webhook test : com.wix.hive.webhooks.WebhooksIT


### Submitting an Issue

We use the GitHub issue tracker to track bugs and features. Before submitting a bug
report or feature request, check to make sure it hasn't already been submitted.
When submitting a bug report, please include a Gist that includes a stack trace and
any details that may be necessary to reproduce the bug, including your Scala version,
and operating system. Ideally, a bug report should include a pull request with
failing specs.

### Submitting a Pull Request

1. Fork it ( https://github.com/[my-github-username]/wix-hive-scala/fork )
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Add specs for your unimplemented feature or bug fix. (**Note:** When
developing a new API a `e2e` test is mandatory.)
4. Run `mvn clean install`. If your specs pass, return to step 3.
5. Implement your feature or bug fix.
6. Run `mvn clean install`. If your specs fail, return to step 5.
7. Commit your changes (`git commit -am 'Add some feature'`)
8. Push to the branch (`git push origin my-new-feature`)
9. Create a new [Pull Request](http://help.github.com/send-pull-requests/)
