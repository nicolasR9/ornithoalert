# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

OrnithoAlert is a Java application for analyzing bird sighting data from ornitho.de (a European bird observation platform). The application scrapes bird observation data, filters out species the user has already sighted, and provides various analysis and visualization tools for identifying interesting birding opportunities.

## Build System & Development Commands

This is a Maven project using Java 17:

```bash
# Compile the project
mvn compile

# Run tests (if any exist)
mvn test

# Package into JAR
mvn package

# Clean build artifacts
mvn clean

# Run specific main classes
mvn exec:java -Dexec.mainClass="com.nirocca.ornithoalert.Main" -Dexec.args="--url GROSSRAUM_LAST_8_DAYS --sort SPECIES"
mvn exec:java -Dexec.mainClass="com.nirocca.ornithoalert.grid.GridMain"
mvn exec:java -Dexec.mainClass="com.nirocca.ornithoalert.location.SpecificSpeciesMain"
```

## Architecture Overview

### Core Components

**Main Classes:**
- `Main.java` - Primary entry point for analyzing recent sightings in a region
- `grid/GridMain.java` - Analyzes Germany in 20x20km grid squares to find optimal birding locations by month
- `location/SpecificSpeciesMain.java` - Tracks specific target species across multiple years
- `RareMain.java` - Focuses on rare species analysis

**Data Flow:**
1. **Web Scraping**: `OrnithoPageReader` fetches HTML from ornitho.de URLs
2. **Data Parsing**: `RegionLastSightingsReader` extracts structured data from the HTML/JSON responses using Jackson
3. **Personal Filter**: `MySightingsReader` reads user's already-seen species from ornitho.de (requires authentication cookies in `/cookies.txt`)
4. **Filtering**: `SightingFilter` removes irrelevant sightings
5. **Output**: `CoordinatesExporter` generates GPS coordinates for visualization tools

### Key Model Classes

**Core Models:**
- `model/Sighting.java` - Main data structure representing a bird observation
- `model/Coordinates.java` - GPS coordinates with precision indicator
- `model/ornitho/` package - Jackson-annotated classes for parsing ornitho.de JSON responses

**Configuration:**
- `Constants.java` - Contains output directory, species color mappings, and enums
- `OrnithoUrl.java` - Predefined URLs for different regions and time ranges

### Package Structure

- `com.nirocca.ornithoalert` - Core application logic
- `model/` - Data structures and DTOs
- `model/ornitho/` - JSON parsing models for ornitho.de API responses
- `grid/` - Grid-based geographical analysis
- `location/` - Location and species-specific analysis
- `frequencies/` - Temporal analysis tools
- `statistics/` - Statistical analysis utilities
- `util/` - Utility classes for filtering, scoring, and data conversion

## Key Dependencies

- **Jackson** (2.19.2) - JSON parsing for ornitho.de API responses
- **Apache HttpClient** (4.5.14) - Web scraping and HTTP requests
- **JPX** (1.7.0) - GPX file generation for GPS visualization
- **Spatial4J** (0.5) - Geospatial calculations for grid analysis
- **Commons CLI** (1.4) - Command-line argument parsing
- **JUnit Jupiter** (5.10.3) - Testing framework

## Authentication Requirements

The application requires authentication cookies for ornitho.de to access personal sighting data. These must be stored in `src/main/resources/cookies.txt` and can be obtained from browser developer tools when logged into ornitho.de.

## Output Generation

The application generates various output formats:
- **Console output**: Formatted sighting lists with markdown links
- **GPS coordinates**: Text files for GPS Visualizer and similar tools
- **GPX files**: For importing into GPS devices and mapping software
- **Statistical analysis**: Species frequency and location scoring data

All outputs are written to the directory specified in `Constants.OUTPUT_DIR` (currently `/Users/nicolas.rocca/tmp/ornithoalert/`).

## Running the Application

The main entry points support different use cases:

1. **Recent sightings analysis**: Use `Main.java` with URL and sorting options
2. **Grid-based hotspot analysis**: Use `grid/GridMain.java` for geographical analysis
3. **Species-specific tracking**: Use `location/SpecificSpeciesMain.java` for target species
4. **Rare species focus**: Use `RareMain.java` for uncommon observations

The application uses extensive parallel processing for web scraping to improve performance when fetching large datasets from ornitho.de.