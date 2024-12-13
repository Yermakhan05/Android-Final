from .models import Medics, Sessions, Client, Hospital, Pharmacy
from .serializer import MedicsSerializer, SessionsSerializer, ClientSerializer, HospitalSerializer, PharmacySerializer
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework import status
from rest_framework.pagination import PageNumberPagination
from django.db.models import Q
from django.utils.dateparse import parse_datetime


class MedicPagination(PageNumberPagination):
    page_size = 15
    page_size_query_param = 'page_size'
    max_page_size = 100


@api_view(['GET'])
def medics_all_list(request):
    if request.method == 'GET':
        medics = Medics.objects.all()
        serializer = MedicsSerializer(medics, many=True)
        return Response(serializer.data)


@api_view(['GET', 'POST'])
def medics_list(request):
    if request.method == 'GET':
        search_query = request.GET.get('search', '')
        hospital_id = request.GET.get('hospital', None)
        city = request.GET.get('city', '')

        medics = Medics.objects.all()
        medics = medics.filter(city=city)
        if search_query:
            medics = medics.filter(Q(medic_name__icontains=search_query) | Q(speciality__icontains=search_query))

        if hospital_id:
            medics = Medics.objects.all()
            medics = medics.filter(hospital_id=hospital_id)

        paginator = MedicPagination()
        paginated_medics = paginator.paginate_queryset(medics, request)
        serializer = MedicsSerializer(paginated_medics, many=True)

        return paginator.get_paginated_response(serializer.data)

    elif request.method == 'POST':
        serializer = MedicsSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


@api_view(['GET', 'POST'])
def sessions_list(request):
    if request.method == 'GET':
        user = request.GET.get('user', None)
        medic = request.GET.get('medic', None)
        date = request.GET.get('date', None)
        sessions = Sessions.objects.all()

        if user:
            sessions = sessions.filter(client_id=user)
        if medic:
            sessions = sessions.filter(medics_id=medic)
        if date:
            sessions = sessions.filter(appointment__date=date)

        serializer = SessionsSerializer(sessions, many=True)

        return Response(serializer.data)

    elif request.method == 'POST':
        if 'medics_id' not in request.data:
            return Response({"error": "medics_id field is required"}, status=status.HTTP_400_BAD_REQUEST)

        client_id = request.data.get('client_id', None)
        if client_id:
            try:
                client = Client.objects.get(id=client_id)
            except Client.DoesNotExist:
                return Response({"error": "Client not found"}, status=status.HTTP_404_NOT_FOUND)
        else:
            return Response({"error": "client_id field is required"}, status=status.HTTP_400_BAD_REQUEST)

        try:
            medic = Medics.objects.get(id=request.data['medics_id'])
        except Medics.DoesNotExist:
            return Response({"error": "Medic not found"}, status=status.HTTP_404_NOT_FOUND)

        appointment = request.data.get('appointment', None)
        if appointment:
            appointment = parse_datetime(appointment)
            if not appointment:
                return Response({"error": "Invalid date format. Use ISO 8601 format (YYYY-MM-DDTHH:MM:SS)."},
                                status=status.HTTP_400_BAD_REQUEST)
        else:
            return Response({"error": "appointment field is required"}, status=status.HTTP_400_BAD_REQUEST)

        session_data = {
            'medics': medic.id,
            'client': client.id,
            'appointment': appointment
        }
        serializer = SessionsSerializer(data=session_data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


@api_view(['GET', 'PUT', 'DELETE'])
def sessions_detail(request, pk):
    try:
        session = Sessions.objects.get(pk=pk)
    except Sessions.DoesNotExist:
        return Response(status=status.HTTP_404_NOT_FOUND)

    if request.method == 'GET':
        serializer = SessionsSerializer(session)
        return Response(serializer.data)
    elif request.method == 'PUT':
        serializer = SessionsSerializer(session, data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
    elif request.method == 'DELETE':
        session.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)


@api_view(['GET', 'POST'])
def client_list(request):
    if request.method == 'GET':
        clients = Client.objects.all()
        serializer = ClientSerializer(clients, many=True)
        return Response(serializer.data)
    elif request.method == 'POST':
        serializer = ClientSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


@api_view(['GET', 'PUT', 'DELETE'])
def client_detail(request, pk):
    try:
        client = Client.objects.get(pk=pk)
    except Client.DoesNotExist:
        return Response(status=status.HTTP_404_NOT_FOUND)

    if request.method == 'GET':
        serializer = ClientSerializer(client)
        return Response(serializer.data)
    elif request.method == 'PUT':
        serializer = ClientSerializer(client, data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
    elif request.method == 'DELETE':
        client.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)


@api_view(['GET'])
def hospital_list(request):
    search_query = request.GET.get('search', '')
    city = request.GET.get('city', '')

    hospitals = Hospital.objects.all()

    if search_query:
        hospitals = hospitals.filter(name__icontains=search_query)

    if city:
        hospitals = hospitals.filter(city__icontains=city)

    hospitals_data = []
    for hospital in hospitals:
        hospital_data = HospitalSerializer(hospital).data
        hospitals_data.append(hospital_data)

    return Response(hospitals_data)


@api_view(['GET'])
def pharmacy_list(request):
    search_query = request.GET.get('search', '')
    city = request.GET.get('city', '')

    pharmacies = Pharmacy.objects.all()

    if search_query:
        pharmacies = pharmacies.filter(name__icontains=search_query)

    if city:
        pharmacies = pharmacies.filter(address__contains=city)

    serializer = PharmacySerializer(pharmacies, many=True)
    return Response(serializer.data)


@api_view(['POST'])
def add_favorite_medic(request):
    client_id = request.data.get('client_id')
    medic_id = request.data.get('medic_id')

    if not client_id or not medic_id:
        return Response({"error": "client_id and medic_id are required."}, status=status.HTTP_400_BAD_REQUEST)

    try:
        client = Client.objects.get(id=client_id)
        medic = Medics.objects.get(id=medic_id)
    except (Client.DoesNotExist, Medics.DoesNotExist):
        return Response({"error": "Client or Medic not found."}, status=status.HTTP_404_NOT_FOUND)

    client.favorite_medics.add(medic)
    client.save()

    return Response(
        {"message": f"Medic '{medic.medic_name}' has been added to client {client.client_name}'s favorites."},
        status=status.HTTP_200_OK)


@api_view(['GET'])
def list_favorite_medics(request, client_id):
    try:
        client = Client.objects.get(id=client_id)
    except Client.DoesNotExist:
        return Response({"error": "Client not found."}, status=status.HTTP_404_NOT_FOUND)

    favorite_medics = client.favorite_medics.all()

    serializer = MedicsSerializer(favorite_medics, many=True)

    return Response(serializer.data)


@api_view(['POST'])
def add_favorite_hospital(request):
    client_id = request.data.get('client_id')
    hospital_id = request.data.get('hospital_id')

    if not client_id or not hospital_id:
        return Response({"error": "client_id and hospital_id are required."}, status=status.HTTP_400_BAD_REQUEST)

    try:
        client = Client.objects.get(id=client_id)
        hospital = Hospital.objects.get(id=hospital_id)
    except (Client.DoesNotExist, Hospital.DoesNotExist):
        return Response({"error": "Client or Hospital not found."}, status=status.HTTP_404_NOT_FOUND)

    client.favorite_hospitals.add(hospital)
    client.save()

    return Response(
        {"message": f"Hospital '{hospital.name}' has been added to client {client.client_name}'s favorites."},
        status=status.HTTP_200_OK)


@api_view(['GET'])
def list_favorite_hospitals(request, client_id):
    try:
        client = Client.objects.get(id=client_id)
    except Client.DoesNotExist:
        return Response({"error": "Client not found."}, status=status.HTTP_404_NOT_FOUND)

    favorite_hospitals = client.favorite_hospitals.all()

    serializer = HospitalSerializer(favorite_hospitals, many=True)

    return Response(serializer.data)


@api_view(['POST'])
def remove_favorite_medic(request):
    client_id = request.data.get('client_id')
    medic_id = request.data.get('medic_id')

    if not client_id or not medic_id:
        return Response({"error": "client_id and medic_id are required."}, status=status.HTTP_400_BAD_REQUEST)

    try:
        client = Client.objects.get(id=client_id)
        medic = Medics.objects.get(id=medic_id)
    except (Client.DoesNotExist, Medics.DoesNotExist):
        return Response({"error": "Client or Medic not found."}, status=status.HTTP_404_NOT_FOUND)

    client.favorite_medics.remove(medic)
    client.save()

    return Response(
        {"message": f"Medic '{medic.medic_name}' has been removed from client {client.client_name}'s favorites."},
        status=status.HTTP_200_OK)


@api_view(['POST'])
def remove_favorite_hospital(request):
    client_id = request.data.get('client_id')
    hospital_id = request.data.get('hospital_id')

    if not client_id or not hospital_id:
        return Response({"error": "client_id and hospital_id are required."}, status=status.HTTP_400_BAD_REQUEST)

    try:
        client = Client.objects.get(id=client_id)
        hospital = Hospital.objects.get(id=hospital_id)
    except (Client.DoesNotExist, Hospital.DoesNotExist):
        return Response({"error": "Client or Hospital not found."}, status=status.HTTP_404_NOT_FOUND)

    client.favorite_hospitals.remove(hospital)
    client.save()

    return Response(
        {"message": f"Hospital '{hospital.name}' has been removed from client {client.client_name}'s favorites."},
        status=status.HTTP_200_OK)
