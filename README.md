# Death Comp Mod

## Description

Simple mod that uses the objectives `ts_PlayTime` and `ts_Deaths` (provided by the Vanilla Tweaks 'Track Raw Statistics' datapack) to list players in order of 'deaths per playtime'.

It attempts to fairly incorporate players with zero deaths but limited playtime by using the following formula to list the players:

```
E ^ deaths / playtime
```

## Dependencies

- fabric-api (required)
- VanillaTweaks 'Track Raw Statistics' datapack (the mod will load ok without it but will do nothing)

## Usage

`/deathcomp`
