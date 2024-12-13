from django.db import models


class Client(models.Model):
    client_name = models.CharField(max_length=255)

    def __str__(self):
        return self.client_name


class Hospital(models.Model):
    name = models.CharField(max_length=255)
    street_address = models.CharField(max_length=255)
    city = models.CharField(max_length=100)
    bed_count = models.PositiveIntegerField()
    image_url = models.URLField(max_length=500, blank=True)
    rating = models.FloatField()
    favorites = models.ManyToManyField(Client, related_name="favorite_hospitals", blank=True)

    def __str__(self):
        return self.name


class Pharmacy(models.Model):
    name = models.CharField(max_length=255)
    address = models.CharField(max_length=255)
    image_url = models.URLField(max_length=500, blank=True)
    rating = models.FloatField()
    phone_number = models.CharField(max_length=15)
    email = models.EmailField()

    def __str__(self):
        return self.name


class Medics(models.Model):
    medic_name = models.CharField(max_length=255, default="Dr. ")
    speciality = models.CharField(max_length=255, default="")
    medic_image = models.CharField(max_length=255, default="")
    price = models.IntegerField(default=0)
    hospital = models.ForeignKey(Hospital, on_delete=models.SET_NULL, null=True, blank=True)
    favorites = models.ManyToManyField(Client, related_name="favorite_medics", blank=True)
    city = models.CharField(max_length=255, null=True)

    def __str__(self):
        return self.speciality + " " + self.medic_name


class Sessions(models.Model):
    medics = models.ForeignKey(Medics, on_delete=models.CASCADE)
    client = models.ForeignKey(Client, on_delete=models.CASCADE)
    appointment = models.DateTimeField(null=True, blank=True)

    def __str__(self):
        return f"{self.medics.medic_name} ({self.medics.speciality}) with {self.client.client_name}"
