import React, { useState, useEffect } from 'react';
import {
    MapPin,
    Truck,
    Package,
    Calendar,
    Clock,
    Navigation,
    CheckCircle,
    ArrowForward,
    Car,
    ClipboardList,
    AlertCircle
} from 'lucide-react';

// Import compatible UI components
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Avatar } from '@/components/ui/avatar';
import { Separator } from '@/components/ui/separator';
import { Progress } from '@/components/ui/progress';

const DriverTripsDashboard = () => {
    const [activeTab, setActiveTab] = useState("active");
    const [activeTrips, setActiveTrips] = useState([]);
    const [completedTrips, setCompletedTrips] = useState([]);
    const [plannedTrips, setPlannedTrips] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [selectedTrip, setSelectedTrip] = useState(null);

    useEffect(() => {
        const fetchTrips = async () => {
            try {
                // In a real app, this would be an API call
                const response = await mockFetchDriverTrips();

                setActiveTrips(response.filter(trip => trip.status === 'IN_PROGRESS'));
                setPlannedTrips(response.filter(trip => trip.status === 'PLANNED'));
                setCompletedTrips(response.filter(trip => trip.status === 'COMPLETED'));

                // Select the first active trip by default if available
                if (response.filter(trip => trip.status === 'IN_PROGRESS').length > 0) {
                    setSelectedTrip(response.filter(trip => trip.status === 'IN_PROGRESS')[0]);
                }

                setLoading(false);
            } catch (err) {
                setError("Failed to load trips. Please try again later.");
                setLoading(false);
            }
        };

        fetchTrips();
    }, []);

    // Mock API response for testing
    const mockFetchDriverTrips = () => {
        return new Promise((resolve) => {
            setTimeout(() => {
                resolve([
                    {
                        id: "1",
                        routeId: "route-123",
                        routeName: "HCM Downtown to District 7",
                        status: "IN_PROGRESS",
                        startTime: "2025-03-21T08:30:00Z",
                        currentStopIndex: 1,
                        totalStops: 4,