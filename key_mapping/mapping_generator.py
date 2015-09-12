__author__ = 'jludvice'

# This is script for generating mapping between keyboard keycodes and symbols that should be printed.
# It expects linux header file input.h and writes mapping as java properties file

import re

regex = re.compile('#define +KEY_(?P<key>\w+)[ \t]+(?P<code>\w+)')
SOURCE_FILE_PATH = 'input.h'
PREFIX = 'KEY_'
CODE_INDEX = len(PREFIX) - 1
DEST_PROPERTY_FILE_PATH = 'mapping.properties'

with open(SOURCE_FILE_PATH) as header_file:
    # result = [...('ESC', '1'), ('1', '2'), ('2', '3')...]
    mapping = regex.findall(header_file.read())
    # result = { ... 'ESC': '1', '1': '2', '2': '3', ...}
    result_dict = dict(mapping)

    # need dict for lookup of keys, but also original list to preserve order of entries
    # (for easier comparsion with original header file)

    with open(DEST_PROPERTY_FILE_PATH, 'w') as properties_file:
        for key, code in mapping:

            if code.startswith(PREFIX):
                # corner cases like this:
                # define KEY_ROTATE_DISPLAY	153	/* Display orientation for e.g. tablets */
                # define KEY_DIRECTION		KEY_ROTATE_DISPLAY

                # strip KEY_ from code
                searched_key = code[CODE_INDEX:]
                # always print key=code, don't reference another key

                properties_file.write("%s=%s\n" % (key, result_dict.get(searched_key, '')))
            else:
                properties_file.write("%s=%s\n" % (key, code))
