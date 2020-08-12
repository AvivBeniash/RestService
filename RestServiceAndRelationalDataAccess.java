package com.example.restservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class RestServiceAndRelationalDataAccess implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(RestServiceAndRelationalDataAccess.class);

  public static void main(String args[]) {
    SpringApplication.run(RestServiceAndRelationalDataAccess.class, args);
  }

  @Autowired
  JdbcTemplate jdbcTemplate;

  @Override
  public void run(String... strings) throws Exception {

    log.info("Creating tables");

    jdbcTemplate.execute("DROP TABLE beers IF EXISTS");
    jdbcTemplate.execute("CREATE TABLE beers(" +
        "id SERIAL, beerName VARCHAR(255), alcohol VARCHAR(255))");

    // Split up the array of whole names into an array of first/last names
    List<Object[]> splitUpBeers = Arrays.asList("Tuborg 6.8", "Stella 4.8", "Corona 4.6", "Heineken 5.0", "Goldstar 4.9").stream()
        .map(beer -> beer.split(" "))
        .collect(Collectors.toList());

    // Use a Java 8 stream to print out each tuple of the list
    splitUpBeers.forEach(beer -> log.info(String.format("Inserting beer record for %s %s", beer[0], beer[1])));

    // Uses JdbcTemplate's batchUpdate operation to bulk load data
    jdbcTemplate.batchUpdate("INSERT INTO beers( beerName, alcohol) VALUES (?,?)", splitUpBeers);

    log.info("Querying for beer records where beerName = 'Corona':");
    jdbcTemplate.query(
        "SELECT id, beerName, alcohol FROM beers WHERE beerName = 'Corona'", //to edit it to be vulnerable.
        (rs, rowNum) -> new BeerBrand(rs.getLong("id"), rs.getString("beerName"), rs.getString("alcohol"))
    ).forEach(customer -> log.info(customer.toString()));
  }
}

@RestController
class BeerBrandController {

  private static final String template = "The beer you choose is: %s!";
  private final AtomicLong counter = new AtomicLong(); //will increase the id value every request

  @GetMapping("/BeerBrand")
  public BeerBrand BeerBrand(@RequestParam(value = "sql") String name) {
    return new BeerBrand(counter.incrementAndGet(), String.format(template, name), "test");
  }
}