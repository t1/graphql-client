![Build Pipeline](https://github.com/t1/graphql-client/workflows/Build%20Pipeline/badge.svg)

# graphql-client

THIS PROJECT HAS MOVED TO [SMALLRYE-GRAPHQL](https://github.com/smallrye/smallrye-graphql)!

A Java code first GraphQL Client API suggestion for [Microprofile GraphQL](https://github.com/eclipse/microprofile-graphql/issues/185).

Usage:

```java
    @GraphQlClientApi
    public interface SuperHeroesApi {
        List<SuperHero> allHeroes();
    }

    @Inject SuperHeroesApi superHeroesApi;

    List<SuperHero> allHeroes = superHeroesApi.allHeroes();
```
