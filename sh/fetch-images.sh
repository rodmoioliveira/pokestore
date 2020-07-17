for i in $(seq 1 890)
do
  wget -P src/images  https://pokeres.bastionbot.org/images/pokemon/"$i".png
done

# cd src/images
# convert *.png -thumbnail 120x90 -set filename:fname '%t_op' +adjoin '%[filename:fname].png'

