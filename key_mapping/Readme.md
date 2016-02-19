# Key mapping 

## Generating
Key mapping is generated from linux [input.h](https://github.com/torvalds/linux/blob/master/include/uapi/linux/input.h) header file.  
Mapping is fetched with following regular expression:
```regex
#define +KEY_(?<key>\w+)[ \t]+(?<code>\w+)
```
For instance following listing would be matched as `key` = `LEFTSHIFT` and `code` = `42`.
```c
#define KEY_LEFTSHIFT		42
```

**Note**:
There are entires like this.
It will require special handling, when code "string" instead of integer.
```c
#define KEY_ROTATE_DISPLAY	153	/* Display orientation for e.g. tablets */
#define KEY_DIRECTION		KEY_ROTATE_DISPLAY
```