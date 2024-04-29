# Death Comp Mod

## Description

Simple mod that uses the objectives `ts_PlayTime` and `ts_Deaths` (provided by the Vanilla Tweaks 'Track Raw Statistics' datapack) to list players in order of 'deaths per playtime'.

It does this by calculating the average deaths per hour played (using median average) and then orders players based on their delta to the expected number of deaths for their playtime.

## Dependencies

- fabric-api (required)
- VanillaTweaks 'Track Raw Statistics' datapack (the mod will load ok without it but will do nothing)

## Usage

`/deathcomp`
