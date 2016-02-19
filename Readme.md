# Keyboard Listener

Experimental Java 8 library for listening on key events from HID input devices.

It attempts to 
* parse C-structure `input-event` from given HID input device
* emit keyCode
* map keyCode to pressed key according to `mapping.properties`

Mapping from key codes to values is generated from `linux/input.h` header file
```c
/*
 * Copyright (c) 1999-2002 Vojtech Pavlik
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 */
 
 // ...
 #define KEY_ESC			1
 // ...
 
/*
 * The event structure itself
 */
struct input_event {
	struct timeval time;
	__u16 type;
	__u16 code;
	__s32 value;
};
```