# Food Management App for Android  
A simple food inventory app built with the **MVVM (Model–View–ViewModel)** architecture pattern

## Feature
### Food Record Management
- Record and manage food information using the [**Room**](https://developer.android.com/training/data-storage/room) database
- Support full CRUD operations:
  - **Create**, **Update**, **List**, **Delete**, and **Search**

### Backup & Restore
- **Local Backup**  
    - Uses [Android-Room-Database-Backup](https://github.com/rafi0101/Android-Room-Database-Backup) to back up the Room database locally
- **Remote Upload / Download**
    - Supports uploading and downloading backup files via a **RESTful API**
    - Implemented with [Ktor](https://ktor.io/docs/client-create-new-application.html) as the HTTP client for server communication

## Project Architecture
This project follows the **MVVM (Model–View–ViewModel)** pattern
Room entities serve as the core model layer


### Database
- **FoodModel**: Defines entities, cross-reference tables, and a type converter for `Date` objects
    - Relationships
        - `FoodItem` and `FoodTag` : **many-to-many** relationship
    - Entities
        - `FoodItem`
            - `id`: Primary key
            - `name`
            - `category`
            - `expiration`: `Date` type
            - `quantity`
            - `storage`
        - `FoodTag`
            - `id`: Primary key
            - `name`: **unique**
        - `FoodItemTagCrossRef`: PrimaryKey(`foodItemId`, `foodTagId`)
            - `foodItemId`
            - `foodTagId`
    - `Converters`
        - Converts `Date` and `Long` for Room database compatibility
- **ModelDao**: Data Access Object (DAO) declaring all database operations
- **AppDatabase**: Defines the Room database configuration and entry poin
- **AppApplication**: Initializes the database and repository instances for global access
    - Registered in `AndroidManifest.xml`
    - Uses `config_use_room_database` to select which repository implementation to provide.
- **ModelRepository**: Defines the interface for data access methods
    - Use `suspend` functions for one-time data fetches
    - Use `Flow` for continuous data observation
- **RoomRepository**: Implements the repository interface using Room as its data source

### Basic Structure
#### Fragment
- Base Classes
    - **BaseFragment**
        Handles common fragment setup, including view binding, ViewModel, and repository initialization
    - **BaseListFragment** *(extends **BaseFragment**)*
        - Holds `listData`, supplied by the ViewModel
        - Provides list management with `listAdapter` for `recyclerView`
        - Provides a *search* menu and *setting* for navigating to backup page
        - Defines default **Delete** and **Search** behaviors:
            - Swipe to delete triggers `showDeletionDialog`
            - `setUpMenuHost()` forwards search queries to the ViewModel, which updates the `listData` that is observed by **BaseListFragment**
- Derived Fragments
    - **HomeFragment**: Displays the  list
    - **TagFragment**: Displays the `FoodTag` list
    - **FoodEditFormFragment**: Add or edit a `FoodItem`
        - Uses an `AutoCompleteTextView` for tag selection and filtering
    - **BackupFragment**: Triggers database **backup** and **restore**

#### ListAdapter

Adapters for displaying data in `RecyclerView`

- Require an `onItemClicked` callback for item click handling
- Observe data (`LiveData`) from **BaseListFragment**
- Implementations:
    - **FoodItemListAdapter**: Displays FoodItems in **HomeFragment**
    - **TagListAdapter**: Displays Tags in **TagFragment**

#### ViewModel

Handles user actions, navigation, and repository communication

- **BaseViewModel**
    - Provides a `provideFactory()` method to inject repositories when creating ViewModels
    - Each fragment must provide its own ViewModel factory

- **BackupViewModel**
    - Handles backup and restore operations
    - Calls repository methods and displays results using `Toast`

- **FoodEditFormViewModel**
    - `loadFoodItem()`: Loads an existing item by ID for editing
    - Handles **Save** and **Update** operations

- **HomeViewModel**
    - Exposes `foodList` as `LiveData`, backed by a `Flow` from the repository
    - `removeItem()`: Deletes a selected FoodItem
    - `search()`: Updates the query LiveData, automatically refreshing the `foodList`

- **TagViewModel**
    - Similar to `HomeViewModel`, but for managing `FoodTag`
    - `saveTag()`: Creates or updates a tag
    - `search()`: Updates tag list based on query
    - `deleteTag()`: Deletes a tag from the database
