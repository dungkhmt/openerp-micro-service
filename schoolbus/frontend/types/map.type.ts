interface IPosition {
    latitude: number;
    longitude: number;
}

interface IGeometry {
    coordinates: number[];
    type: string;
}

interface IProperties {
    id: string;
    continent: string;
    label: string;
    name: string;
    street: string;
    county: string;
    region: string;
    country: string;
}

interface IFeature {
    geometry: IGeometry;
    properties: IProperties;
    type: string;
}

interface IAutoCompleteGetResponse {
    features: IFeature[];
    type: string;
}

interface IAutoCompleteParams {
    text: string;
    focus?: {
        point?: {
            lon?: number | null;
            lat?: number | null;
        } | null;
    } | null;
    boundary?: {
        rect?: {
            min_lon?: number | null;
            min_lat?: number | null;
            max_lon?: number | null;
        } | null;
        country?: string | null;
    } | null;
}

interface ISearchGetResponse {
    features: IFeature[];
    type: string;
}
interface ISearchParams {
    text: string;
    focus?: {
        point?: {
            lon?: number | null;
            lat?: number | null;
        } | null;
    } | null;
    boundary?: {
        rect?: {
            min_lon?: number | null;
            min_lat?: number | null;
            max_lon?: number | null;
        } | null;
        country?: string | null;
    } | null;
}

interface IDirectionsParams {
    coordinates: number[][];
}

interface IDirectionSummary {
    distance: number;
    duration: number;
}

interface IDirectionsRoute {
    geometry: string;
    segments: {
        distance: number;
        duration: number;
        steps: {
            distance: number;
            duration: number;
            type: string;
            instruction: string;
            way_points: [number, number][];
        }[];
    }[];
    summary: IDirectionSummary;
}

interface IDirectionsGetResponse {
    routes: IDirectionsRoute[];
}