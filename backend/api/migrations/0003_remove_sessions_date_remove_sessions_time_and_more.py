# Generated by Django 4.2.17 on 2024-12-09 12:03

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('api', '0002_hospital_favorites_medics_favorites'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='sessions',
            name='date',
        ),
        migrations.RemoveField(
            model_name='sessions',
            name='time',
        ),
        migrations.AddField(
            model_name='sessions',
            name='appointment',
            field=models.DateTimeField(default='2024-12-15 14:30:00'),
            preserve_default=False,
        ),
    ]
