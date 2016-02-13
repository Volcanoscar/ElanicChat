from PIL import Image
from resizeimage import resizeimage
import os
import sys

direc = sys.argv[1]
divider = float(sys.argv[2])
absdirec = os.path.abspath(direc)
new_direc = os.path.join(absdirec, 'results')

print new_direc, "new_direc"

if not os.path.isdir(new_direc):
	os.mkdir(new_direc)

files = os.listdir(absdirec)
for f in files:

	if not f.endswith('.png'):
		continue


	if f.endswith('.9.png'):
		continue

	fd = open(os.path.join(absdirec, f), 'r+b')
	image = Image.open(fd)
	width = image.size[0]
	new_width = int(width / divider)
	res_img = resizeimage.resize_width(image, [new_width, new_width], validate=False)

	words = f.split("_")
	words_size = int(words[-1].split("dp")[0])
	new_size = str(int(words_size/divider))

	new_words = words[:-1] + ["dp".join([new_size, '.png'])]
	new_file = "_".join(new_words)

	res_img.save(os.path.join(new_direc, new_file), image.format)
	print "saved image", os.path.join(new_direc, new_file)

