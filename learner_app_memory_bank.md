# Learner App - Memory Bank

## Overview
The Learner app is an Android vocabulary learning application designed to help users learn foreign words (primarily English) through spaced repetition and interactive exercises. The app uses a SQLite database to store words and implements a sophisticated scheduling system for optimal learning retention.

## Core Architecture

### Main Components
1. **MainActivity** - Central activity managing fragments and app lifecycle
2. **DBHelper** - Database management and word operations
3. **Fragment-based UI** - Multiple fragments for different functionalities
4. **AlarmManagerBroadcastReceiver** - Notification system for daily reminders

### Key Classes and Responsibilities

#### MainActivity.java
- **Purpose**: Main entry point, fragment navigation, settings management
- **Key Features**:
  - Fragment navigation using FragmentListener interface
  - Text-to-speech integration for pronunciation
  - Notification scheduling (morning/evening reminders)
  - Database backup/restore functionality
  - Ad management integration

#### DBHelper.java
- **Purpose**: SQLite database operations and word management
- **Database Schema**:
  ```sql
  CREATE TABLE words (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    english TEXT NOT NULL,
    russian TEXT NOT NULL,
    transcription TEXT,
    translations TEXT, -- favorites flag
    date TEXT NOT NULL   -- learning date/status
  );
  ```
- **Data Organization**:
  - `date = 1`: Unknown words (not yet studied)
  - `date = 0`: Known words (marked as already known)
  - `date = YYYYMMDD`: Learned words with specific study date
- **Key Collections**:
  - `listUnknownWords`: Words with date = 1
  - `listKnownWords`: Words with date = 0
  - `learnedWords`: All words with actual dates
  - `uploadDb`: TreeMap organizing all words by date

## Core Learning Logic

### Spaced Repetition System
The app implements a spaced repetition schedule for optimal retention:
- **Day 1**: Initial learning
- **Day 2**: First repetition (next day)
- **Day 3**: Second repetition (2 days after initial)
- **Day 21**: Third repetition (20 days after initial)
- **Day 90**: Fourth repetition (89 days after initial)

### Word Learning Flow
1. **Word Selection**: Random selection from unknown words pool
2. **Learning Decision**: User marks word as "Know" or "Don't Know"
3. **Database Update**: 
   - "Don't Know" → date = current date (YYYYMMDD)
   - "Know" → date = 0 (known words list)
4. **Scheduling**: Words become available for review on scheduled days

### Daily Learning Process
Words scheduled for today are presented in two modes:
1. **Card Mode**: Show word with translation (alternating display count)
2. **Spelling Mode**: Interactive letter-by-letter spelling exercise

## Fragment Architecture

### Navigation Fragments
- **FrgMainMenu**: Main navigation hub with 5 primary actions
- **FrgRepeatMenu**: Review options by specific dates or calendar

### Learning Fragments
- **FrgAddWordForStudy**: Select unknown words from dictionary
- **FrgAddOwnWordToBase**: Add custom vocabulary entries
- **FrgLearnToDay**: Daily learning session with dual modes
- **FrgRepeatDay**: Review words from specific dates

### Management Fragments
- **FrgCardAllWords**: Browse all learned words as cards
- **FrgListAllWord**: List view with search, sort, and edit capabilities
- **FrgCardFavorite**: Favorite words management
- **FrgCalendar**: Calendar-based word review selection

## Key Data Structures

### Word Array Structure
Each word is represented as String[] with indices:
- `[0]`: English word
- `[1]`: Russian translation
- `[2]`: Transcription
- `[3]`: Database ID
- `[4]`: Favorites flag ("true"/"false")
- `[5]`: Date string

### Database Collections
```java
TreeMap<Integer, ArrayList<String[]>> uploadDb;
// Key: Date as Integer (0, 1, or YYYYMMDD)
// Value: List of word arrays for that date

ArrayList<String[]> listUnknownWords;  // date = 1
ArrayList<String[]> listKnownWords;    // date = 0  
ArrayList<String[]> learnedWords;      // all dated words
```

## UI Flow Patterns

### Main Menu Navigation
```
Main Menu → 5 Primary Actions:
├── Add New Word → FrgAddWordForStudy
├── Learn Today → FrgLearnToDay (if words available)
├── Repeat Studied → FrgRepeatMenu
├── Add Custom Word → FrgAddOwnWordToBase
└── Settings → Drawer Menu
```

### Learning Session Flow
```
FrgLearnToDay:
├── Check word alternation count
├── If alternation remaining:
│   └── Card Mode (show word/translation)
└── If alternation complete:
    └── Spelling Mode (interactive letter input)
```

### Repeat Menu Options
```
FrgRepeatMenu → Date-based Review:
├── Today (Day 1)
├── Yesterday (Day 2)  
├── 2 Days Ago (Day 3)
├── 21 Days Ago
├── 90 Days Ago
├── Calendar Selection
└── All Words Browser
```

## Android-Specific Features

### Notification System
- **AlarmManagerBroadcastReceiver**: Schedules daily reminders
- **Morning Alarm**: 7:00 AM reminder
- **Evening Alarm**: 8:00 PM reminder
- **PendingIntent**: Opens main activity when notification tapped

### Text-to-Speech Integration
- **Automatic pronunciation**: Configurable auto-speech setting
- **Manual pronunciation**: Audio button on word cards
- **Language**: Set to English locale

### Database Management
- **Asset Database**: Pre-loaded 12,000 word dictionary
- **Backup/Restore**: Export/import database files
- **Auto-creation**: Copies from assets on first run

## Settings and Preferences

### User Configurable Options
- **Auto Speech**: Automatic word pronunciation
- **Word Order**: English→Russian vs Russian→English
- **Word Alternation**: Cards vs spelling exercise ratio (1-10)
- **Notifications**: Morning/evening reminder toggles
- **Database**: Backup/restore functionality

### Persistence
- **SharedPreferences**: User settings storage
- **Keys**:
  - `autoSpeech`: Boolean for auto-pronunciation
  - `changeWordPlace`: Boolean for word order
  - `WordAlternation`: Integer for exercise alternation
  - `notify_morning/evening`: Boolean for notifications

## Error Handling and Edge Cases

### Common Scenarios
- **No words today**: Toast message when daily list is empty
- **No unknown words**: Navigation back to main menu
- **Database corruption**: Asset database restoration
- **Permission requests**: Storage access for backup/restore
- **Long words**: Automatic skip for words exceeding screen width

### State Management
- **Fragment lifecycle**: Proper cleanup in onDestroy/onStop
- **Memory management**: Static collections for performance
- **App restart**: Required after database restore operations

## Performance Considerations

### Memory Optimization
- **Singleton DBHelper**: Single database instance
- **Static collections**: Cached word lists in memory
- **TreeMap organization**: Efficient date-based lookups
- **Fragment reuse**: Detach/attach pattern for updates

### Database Efficiency
- **Batch operations**: Single transaction for multiple updates
- **Indexed queries**: Primary key and date-based searches
- **Prepared statements**: Parameter binding for security

This memory bank captures the essential architecture and functionality of the Learner app, providing a comprehensive reference for understanding its core learning logic, UI flows, data management, and Android-specific implementations.
