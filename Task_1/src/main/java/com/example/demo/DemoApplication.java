
              package com.example.demo;
              import org.springframework.boot.SpringApplication;
              import org.springframework.boot.autoconfigure.SpringBootApplication;
              import org.springframework.web.bind.annotation.GetMapping;
              import org.springframework.web.bind.annotation.RequestParam;
              import org.springframework.web.bind.annotation.RestController;

              @SpringBootApplication
              @RestController
              public class DemoApplication {


                  public static void main(String[] args) {
                  SpringApplication.run(DemoApplication.class, args);
                  }

									@GetMapping("/")
									public String hello1(@RequestParam(value = "name", defaultValue = "World") String name) {
                  return String.format("Hello %s!", name);
                  }

                  @GetMapping("/hello")
                  public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
                  return String.format("Hello %s!", name);
                  }

                  //@GetMapping("/test")
                  //public String test(@RequestParam(value = "name", defaultValue = "Test") String name) {
                  //return String.format("Hello %s!", name);
                  //}

                  

              }
